package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.LOG
import calebxzhou.craftcone.server.model.ConePlayer
import calebxzhou.craftcone.server.model.ConePlayer.Companion.PWD_FILE
import java.io.FileNotFoundException
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.util.*

/**
 * Created  on 2023-07-13,17:27.
 */
//玩家登录请求
data class LoginC2SPacket(
    //玩家UUID
    val pid: UUID,
    //密码
    val pwd: String,
) : C2SPacket{
    companion object : ReadablePacket<LoginC2SPacket>{
        override fun read(buf: FriendlyByteBuf): LoginC2SPacket {
            //for server
            return LoginC2SPacket(buf.readUUID(),buf.readUtf())
        }

    }
    override fun process(clientAddress: InetSocketAddress) {
        var packet: LoginS2CPacket
        try {
            val pwd = Files.readString(ConePlayer.getProfilePath(pid).resolve(PWD_FILE))
            if(this.pwd == pwd){
                packet = LoginS2CPacket(true,"")
                LOG.info("$pid 已登录")
                ConePlayer.addOnlinePlayer( ConePlayer(pid,pwd,clientAddress) )
            }else{
                packet = LoginS2CPacket(false,"密码错误")
            }
        }
        catch (e: FileNotFoundException){
            packet = LoginS2CPacket(false,"玩家不存在")
        }
        catch (e: NoSuchFileException){
            packet = LoginS2CPacket(false,"玩家不存在")
        }
        catch (e: Exception) {
            packet = LoginS2CPacket(false,e.localizedMessage)
        }
        ConeNetManager.sendPacket(packet,clientAddress)


    }


}