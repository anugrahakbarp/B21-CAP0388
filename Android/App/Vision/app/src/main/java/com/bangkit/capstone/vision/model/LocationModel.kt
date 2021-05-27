package com.bangkit.capstone.vision.model

import java.io.Serializable

class LocationModel : Serializable {
    var locationAddress: String = ""
    var locationAdmin: String = ""
    var locationSubAdmin: String = ""
    var locationLocality: String = ""
    var locationSubLocality: String = ""
    var locationLatitude: String = ""
    var locationLongitude: String = ""
}