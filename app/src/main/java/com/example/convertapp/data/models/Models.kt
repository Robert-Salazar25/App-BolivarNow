data class CurrencyResponse(
    val datetime: Datetime,
    val monitors: Map<String, Monitor>
)

data class Datetime(
    val date: String,
    val time: String
)

data class Monitor(
    val change: Double,
    val color: String,
    val image: String?,
    val last_update: String,
    val percent: Double,
    val price: Double,
    val price_old: Double,
    val symbol: String,
    val title: String
)
