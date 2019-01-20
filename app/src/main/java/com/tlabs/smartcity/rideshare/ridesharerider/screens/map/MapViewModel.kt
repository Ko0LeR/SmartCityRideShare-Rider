package com.tlabs.smartcity.rideshare.ridesharerider.screens.map

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tlabs.smartcity.rideshare.ridesharerider.api.BackendApi
import com.tlabs.smartcity.rideshare.ridesharerider.api.MapBoxApi
import com.tlabs.smartcity.rideshare.ridesharerider.api.SovrApi
import com.tlabs.smartcity.rideshare.ridesharerider.data.*
import com.tlabs.smartcity.rideshare.ridesharerider.util.ScopedViewModel
import kotlinx.android.synthetic.main.map_frafment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.anko.longToast
import retrofit2.HttpException

class MapViewModel : ScopedViewModel() {
    var start: LatLng? = null
    var finish: LatLng? = null
    var driverDid: String = ""
    private var token: String = ""

    suspend fun buildRoute(): DirectionsRoute = withContext(Dispatchers.IO) {
        MapBoxApi.buildRoute(start!!, finish!!).await()
    }

    suspend fun offerConnection(fragment: Fragment, window: Window) = withContext(Dispatchers.IO) {
        val logResp =
            try {
                SovrApi.instance.login(LoginReq(password = "test123test", username = "verifier")).await()
            } catch (e: Exception) {
                Log.e("OFFER", e.toString())
                disableSpinnerAndWindow(fragment, window, true)
                return@withContext
            }
        token = logResp.token
        val createConnectOfferResponse = try {
            SovrApi.instance.createConnOffer(token, CreateConnectionOfferRequest(Meta(), ConOfferData())).await()
        } catch (e: Exception) {
            Log.e("OFFER", e.toString())
            disableSpinnerAndWindow(fragment, window, true)
            return@withContext
        }

        try {
            BackendApi.instance.requestRide(
                RequestRide(
                    from = Coordinates(
                        longitude = start!!.longitude,
                        latitude = start!!.latitude
                    ),
                    to = Coordinates(
                        longitude = finish!!.longitude,
                        latitude = finish!!.latitude
                    ),
                    message = Gson().toJson(createConnectOfferResponse.message)
                )
            ).await()
        } catch (e: Exception) {
            Log.e("OFFER", e.toString())
            if (e is HttpException && e.code() == 404) {
                toastMessage(fragment.requireActivity(), "There is no available rides. Please, try again later.")
            }
            disableSpinnerAndWindow(fragment, window, true)
            return@withContext
        }

        disableSpinnerAndWindow(fragment, window, true)

        toastMessage(fragment.requireActivity(), "Waiting for pairwise.")
        try {
            waitForPairwiseConnection()
        } catch (e: Exception) {
            Log.e("OFFER", e.toString())
            disableSpinnerAndWindow(fragment, window, true)
            return@withContext
        }

        disableSpinnerAndWindow(fragment, window, false)
        try {
            SovrApi.instance.createProofReq(token, CreateProofReq(driverDid, ProofRequest())).await()
        } catch (e: Exception) {
            Log.e("OFFER", e.toString())
            disableSpinnerAndWindow(fragment, window, true)
            return@withContext
        }

        toastMessage(fragment.requireActivity(), "Waiting for proof.")
        disableSpinnerAndWindow(fragment, window, true)
        try {
            waitForProof(fragment)
        } catch (e: Exception) {
            Log.e("OFFER", e.toString())
            disableSpinnerAndWindow(fragment, window, true)
            return@withContext
        }
    }

    private suspend fun waitForPairwiseConnection() = withContext(Dispatchers.IO) {
        var pairwiseResponse = SovrApi.instance.pairwiseConnections(token).await()
        for (i in 1..120) {
            if (pairwiseResponse.pairwise.isEmpty()) {
                delay(1000)
                pairwiseResponse = SovrApi.instance.pairwiseConnections(token).await()
            } else {
                break
            }
        }
        driverDid = pairwiseResponse.pairwise[0].their_did
    }

    private suspend fun waitForProof(fragment: Fragment) =
        withContext(Dispatchers.IO) {
            var proofs = SovrApi.instance.getAllProof(token).await()
            var proof: RequestedPredicates? = null
            if (proofs.isNotEmpty()) {
                proof = proofs.last()
            }
            for (i in 1..120) {
                if (proofs.isEmpty() || proof!!.status != "received") {
                    delay(1000)
                    proofs = SovrApi.instance.getAllProof(token).await()
                    proof = proofs.last()
                } else {
                    break
                }
            }
            if (proof == null) {
                return@withContext
            }
            val phone = proof.proof.requested_proof.revealed_attrs.attr1_referent.raw

            withContext(Dispatchers.Main) {
                AlertDialog.Builder(fragment.requireContext())
                    .setTitle("Driver phone")
                    .setMessage(phone)
                    .setPositiveButton("Pay!") { dialog, _ ->

                        Log.e("MapViewModel", "In the payment coroutine")
                        try {
                            BackendApi.instance.pay(PayDto())
                        } catch (e: Exception) {
                            Log.e("MapViewModel", "Error with payment! $e")
                        } finally {
                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("Dismiss") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
}

private suspend fun disableSpinnerAndWindow(fragment: Fragment, window: Window, disable: Boolean) =
    withContext(Dispatchers.Main) {
        if (disable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            fragment.progress.visibility = View.GONE
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            fragment.progress.visibility = View.VISIBLE
        }
    }

private suspend fun toastMessage(activity: Activity, str: String) = withContext(Dispatchers.Main) {
    activity.longToast(str)
}

