package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

	public static void main(String[] args) throws IOException {
		//args[0] - path to input
		//args[1] - path and name to output
		Input input = Input.getInstance();
		init(args[0], input);
		LeiaMicroservice leia = new LeiaMicroservice(input.getAttacks());

		Thread tLeia = new Thread(leia);
		Thread tR2d2 = new Thread(input.getR2D2());
		Thread tLando = new Thread(input.getLando());
		Thread tHanSolo = new Thread(new HanSoloMicroservice());
		Thread tC3po = new Thread(new C3POMicroservice());


		tHanSolo.start();
		tC3po.start();
		tLeia.start();
		tR2d2.start();
		tLando.start();

		try {

			tHanSolo.join();
			tC3po.join();
			tLeia.join();
			tR2d2.join();
			tLando.join();
		} catch (InterruptedException e) {}

		createOutput(args[1]);
		System.out.println(	"end!!!!!!!!!!!!!!!!!!!!!!!!!!11");
		//System.exit(0);
		//sysytem doesnt stop here...why??????????????????????????????

	}

	public static void createOutput(String pathToSave) throws IOException{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Diary tempD = Diary.getInstance();
		List<String> output = Arrays.asList("totalAttacks: " + tempD.getTotalAttacks(),
				"HanSoloFinish: " + tempD.getHanSoloFinish(),
				"C3POFinish: " + tempD.getC3POFinish(),
				"R2D2Deactivate: " + tempD.getR2D2Deactivate(),
				"LeiaTerminate: " + tempD.getLeiaTerminate(),
				"HanSoloTerminate: " + tempD.getHanSoloTerminate(),
				"C3POTerminate: " + tempD.getC3POTerminate(),
				"R2D2Terminate: " + tempD.getR2D2Terminate(),
				"LandoTerminate: " + tempD.getLandoTerminate());
		FileWriter fileWriter = new FileWriter(pathToSave);
		gson.toJson(output, fileWriter);
		fileWriter.close();
	}



	public static void init(String argsAt0, Input input) throws IOException {
		Gson gson = new Gson();
		File jsonFIle = Paths.get(argsAt0).toFile();
		try(Reader reader = new FileReader(argsAt0)) {
			JsonObject jObj = gson.fromJson(new FileReader(jsonFIle), JsonObject.class);
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

			input.setAttacks(attacks);
			input.setEwoks(ewoks);
			input.setLando(lando);
			input.setR2D2(r2d2);
			reader.close();


		} catch (Exception e){
			System.out.println("args[0] is not defined");
			e.printStackTrace();
		}
	}
}
