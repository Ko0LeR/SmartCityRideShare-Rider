package com.tlabs.smartcity.rideshare.ridesharerider.api

import android.util.Log
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tlabs.smartcity.rideshare.ridesharerider.util.toPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MapBoxApi {
    fun buildRoute(from: LatLng, to: LatLng): Deferred<DirectionsRoute> {

        val fromPoint = from.toPoint()
        val toPoint = to.toPoint()

        // Initialize the directionsApiClient object for eventually drawing a navigation route on the map
        val directionsApiClient = MapboxDirections.builder()
            .origin(fromPoint)
            .destination(toPoint)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken("pk.eyJ1IjoiYXRvcnViYXIiLCJhIjoiY2pub3AxYXM2MXk0MDNxbDd5YmptNXk3ZiJ9.mogGkAsapJ4C5YADGCBuBg")
            .build()

        val deferred = CompletableDeferred<DirectionsRoute>()
        deferred.invokeOnCompletion {
            if (deferred.isCancelled) {
                directionsApiClient.cancelCall()
            }
        }

        directionsApiClient.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                // Check that the response isn't null and that the response has a route
                when {
                    response.body() == null -> {
                        Log.e(
                            MapBoxApi::javaClass.name,
                            "No routes found, make sure you set the right user and access token."
                        )
                        deferred.completeExceptionally(RuntimeException("No routes found"))
                    }
                    response.body()!!.routes().size < 1 -> {
                        Log.e(MapBoxApi::javaClass.name, "No routes found")
                        deferred.completeExceptionally(RuntimeException("No routes found"))
                    }
                    else -> {
                        val currentRoute = response.body()!!.routes()[0]
                        deferred.complete(currentRoute)
                    }
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                Log.e(MapBoxApi::javaClass.name, "Directions API Failure")
                deferred.completeExceptionally(throwable)
            }
        })

        return deferred
    }

}