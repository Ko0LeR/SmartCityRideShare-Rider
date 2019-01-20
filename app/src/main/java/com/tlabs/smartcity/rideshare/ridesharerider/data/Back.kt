package com.tlabs.smartcity.rideshare.ridesharerider.data

data class RequestRide(
    val from: Coordinates,
    val to: Coordinates,
    val message: String,
    val wallet: String = "0x322DDB258B6A596C332A8E50eB18B6Cc3C975AC7"
)

data class Coordinates(val longitude: Double, val latitude: Double)

data class BalanceResponse(val balance: Int)
