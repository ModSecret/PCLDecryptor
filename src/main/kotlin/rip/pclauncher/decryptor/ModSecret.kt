package rip.pclauncher.decryptor

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import java.awt.SystemColor.text
import java.io.ByteArrayOutputStream
import java.lang.management.ManagementFactory
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec

object ModSecret {
    fun secretDecrypt(sourceString: String, key: String = ""): String {
        val secretKey = getSecretKey(key)
        val keyBytes = secretKey.toByteArray(StandardCharsets.UTF_8)
        val ivBytes = "95168702".toByteArray(StandardCharsets.UTF_8)

        val desKeySpec = DESKeySpec(keyBytes)
        val keyFactory = SecretKeyFactory.getInstance("DES")
        val secretKeyObj = keyFactory.generateSecret(desKeySpec)

        val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, secretKeyObj, ivSpec)

        val decodedBytes = Base64.getDecoder().decode(sourceString)

        ByteArrayOutputStream().use { outputStream ->
            CipherOutputStream(outputStream, cipher).use { cipherStream ->
                cipherStream.write(decodedBytes)
            }
            return outputStream.toString(StandardCharsets.UTF_8.name())
        }
    }

    fun getSecretKey(key: String?): String {
        return when {
            key.isNullOrEmpty() -> "@;$ Abv2"
            else -> {
                val hashString = getHash(key).toString()
                val filledString = strFill(hashString, "X", 8)
                filledString.substring(0, 8)
            }
        }
    }

    fun getHash(str: String): ULong {
        var num: ULong = 5381u
        for (i in str.indices) {
            num = (num shl 5) xor num xor str[i].code.toULong()
        }
        return num xor 12218072394304324399u
    }

    fun strFill(str: String, code: String, length: Byte): String {
        return if (str.length > length) {
            str.substring(0, length.toInt())
        } else {
            str.padEnd(length.toInt(), code[0]).substring(str.length) + str
        }
    }

    fun secretGetUniqueAddress(): String {
        val str = Advapi32Util.registryGetStringValue(
                WinReg.HKEY_LOCAL_MACHINE,
                "SYSTEM\\HardwareConfig", "LastConfig"
            ).uppercase(Locale.getDefault()).trim('{', '}')

        val text = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\PCL", "Identify")

        val combinedHash = getHash(str + text).toString(16).padStart(16, '7').toUpperCase()
        return formatUniqueAddress(combinedHash)
    }

    fun formatUniqueAddress(hash: String): String {
        return "${hash.substring(4, 8)}-${hash.substring(12, 16)}-${hash.substring(0, 4)}-${hash.substring(8, 12)}"
    }


}