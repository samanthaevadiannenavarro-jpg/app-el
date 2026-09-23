package com.example.data.model

data class BinanBarangay(
    val name: String,
    val zipCode: String = "4024",
    val landmark: String,
    val deliveryFee: Double = 49.0,
    val estMinutes: Int = 20,
    val latitude: Double,
    val longitude: Double
)

object BinanLocations {
    // All 24 official Barangays of the City of Biñan, Laguna
    val allBarangays: List<BinanBarangay> = listOf(
        BinanBarangay("Biñan (Poblacion)", "4024", "Plaza Rizal & Historic Alberto Mansion", 39.0, 15, 14.3392, 121.0825),
        BinanBarangay("Bungahan", "4024", "Bungahan Elementary School & Residencia", 49.0, 20, 14.3225, 121.0664),
        BinanBarangay("Canlalay", "4024", "National Highway & Canlalay Bridge", 39.0, 15, 14.3490, 121.0818),
        BinanBarangay("Casile", "4024", "Carmona-Biñan border & Casile Road", 59.0, 30, 14.2810, 121.0250),
        BinanBarangay("De La Paz", "4024", "De La Paz Baywalk & Laguna Lake view", 49.0, 20, 14.3540, 121.0920),
        BinanBarangay("Ganado", "4024", "Laguna Technopark Gate & Industrial Park", 49.0, 22, 14.3120, 121.0540),
        BinanBarangay("San Francisco (Halang)", "4024", "Southwoods City & San Francisco Church", 45.0, 18, 14.3280, 121.0690),
        BinanBarangay("Langkiwa", "4024", "Langkiwa National High School", 55.0, 25, 14.3015, 121.0450),
        BinanBarangay("Loma", "4024", "Biñan Football Stadium & Loma Market", 45.0, 18, 14.3180, 121.0600),
        BinanBarangay("Malaban", "4024", "Malaban Fish Port & Coastal Road", 49.0, 22, 14.3620, 121.0950),
        BinanBarangay("Malamig", "4024", "Brentville International & Southwoods Area", 45.0, 18, 14.3350, 121.0580),
        BinanBarangay("Mampalasan", "4024", "CALAX Mampalasan Exit & Greenfield", 49.0, 22, 14.3160, 121.0720),
        BinanBarangay("Platero", "4024", "Platero Covered Court & A. Bonifacio", 39.0, 15, 14.3430, 121.0870),
        BinanBarangay("Poblacion", "4024", "Biñan City Hall Complex & City Plaza", 39.0, 12, 14.3385, 121.0830),
        BinanBarangay("San Antonio", "4024", "Pavilion Mall & San Antonio Parish", 39.0, 14, 14.3320, 121.0820),
        BinanBarangay("San Jose", "4024", "San Jose Church & Southwoods border", 45.0, 18, 14.3410, 121.0740),
        BinanBarangay("San Vicente", "4024", "Central Flower District & Biñan Public Market", 39.0, 10, 14.3440, 121.0790),
        BinanBarangay("Santo Domingo", "4024", "Santo Domingo Chapel & Laguna Blvd", 49.0, 22, 14.3090, 121.0630),
        BinanBarangay("Santo Niño", "4024", "Santo Niño Parish & Olivarez Plaza", 39.0, 15, 14.3470, 121.0850),
        BinanBarangay("Santo Tomas (Calabuso)", "4024", "Santo Tomas Rural Area & Farm Road", 55.0, 28, 14.2950, 121.0380),
        BinanBarangay("Sorosoro", "4024", "Sorosoro Creek & Villa Biñan", 45.0, 18, 14.3340, 121.0730),
        BinanBarangay("Timbao", "4024", "Timbao Industrial Zone & Barangay Hall", 49.0, 24, 14.3050, 121.0580),
        BinanBarangay("Tubigan", "4024", "Tubigan Subdivision & Biñan Doctors Hospital", 39.0, 14, 14.3360, 121.0780),
        BinanBarangay("Zapote", "4024", "Zapote Multi-Purpose Hall & Manila S Rd", 39.0, 15, 14.3290, 121.0880)
    )

    fun findBarangay(name: String): BinanBarangay {
        return allBarangays.find { it.name.equals(name, ignoreCase = true) } ?: allBarangays[16] // San Vicente default
    }
}
