package gomapservice;

import java.io.IOException;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import com.github.aeonlucid.pogoprotos.GoMap.GetPlayerInfoResponse;

public class PlayerSerializer implements Serializer<Player> {

	@Override
	public Player deserialize(DataInput2 in, int available) throws IOException {
		int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readFully(bytes);
		
        GetPlayerInfoResponse response = GetPlayerInfoResponse.parseFrom(bytes);
        return new Player(response);
	}

	@Override
	public void serialize(DataOutput2 out, Player player) throws IOException {
		byte[] bytes = player.build().toByteArray();
		out.writeInt(bytes.length);
		out.write(bytes);		
	}
}
