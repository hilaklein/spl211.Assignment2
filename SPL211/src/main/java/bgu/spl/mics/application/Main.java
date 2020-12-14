package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.LandoMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) throws IOException {

		Gson gson = new Gson();
		Reader reader = null;
		try{
			reader = new FileReader(args[0]);
			JsonObject jObj = gson.fromJson(reader, JsonObject.class);
			JsonArray jAttacks = jObj.get("attacks").getAsJsonArray();
			Attack[] attacks = new Attack[jAttacks.size()];
			int attackCounter = 0;
			for (JsonElement tempAttack : jAttacks){
				JsonObject currAttack = tempAttack.getAsJsonObject();
				int duration = currAttack.get("duration").getAsInt();
				JsonArray jSerial = currAttack.get("serials").getAsJsonArray();
				List<Integer> serialList = new LinkedList<>();
				for (JsonElement ewokId : jSerial)
					serialList.add(ewokId.getAsInt());
				attacks[attackCounter] = new Attack(serialList, duration);
				attackCounter++;
			}

			R2D2Microservice r2d2 = new R2D2Microservice(jObj.get("R2D2").getAsLong());
			LandoMicroservice lando = new LandoMicroservice(jObj.get("Lando").getAsLong());
			Ewoks ewoks = new Ewoks(jObj.get("Ewoks").getAsInt());


		} catch (Exception e){
			System.out.println("args[0] is not defined");
		}
		reader.close();

		//args[0] - path to input
		//args[1] - path and name to output

	}
}
