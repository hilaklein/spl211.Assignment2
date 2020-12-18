package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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

		createOutput(args[1]);
		System.out.println(	"end!!!!!!!!!!!!!!!!!!!!!!!!!!11");
		//System.exit(0);
		//sysytem doesnt stop here...why??????????????????????????????

	}

	public static void createOutput(String pathToSave) throws IOException{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Diary tempD = Diary.getInstance();
		List<String> output = Arrays.asList("totalAttacks: " + tempD.getTotalAttacks() + ". ",
				"HanSoloFinish: " + tempD.getHanSoloFinish() + ". ",
				"C3POFinish: " + tempD.getC3POFinish() + ". ",
				"R2D2Deactivate: " + tempD.getR2D2Deactivate() + ". ",
				"LeiaTerminate: " + tempD.getLeiaTerminate() + ". ",
				"HanSoloTerminate: " + tempD.getHanSoloTerminate() + ". ",
				"C3POTerminate: " + tempD.getC3POTerminate() + ". ",
				"R2D2Terminate: " + tempD.getR2D2Terminate() + ". ",
				"LandoTerminate: " + tempD.getLandoTerminate() + ". ");
		FileWriter fileWriter = new FileWriter(pathToSave);
		gson.toJson(output, fileWriter);
		fileWriter.close();
	}

	  /*
    ●int totalAttacks - the total number of attacks executed by HanSolo and C3PO.can also be of AtomicInteger type. Stamped only by HanSolo or C3PO ​(!!)​.
    ●long HanSoloFinish - a timestamp indicating when HanSolo finished theexecution of all his attacks.
    ●long C3POFinish - a timestamp indicating when C3PO finished the execution ofall his attacks.
    ●long R2D2Deactivate - a timestamp indicating when R2D2 finished deactivationthe shield generator.
    ●long LeiaTerminate - a time stamp that Leia puts in right before termination.
    ●long HanSoloTerminate - a time stamp that HanSolo puts in right beforetermination.
    ●long C3POTerminate - a time stamp that C3PO puts in right before termination.
    ●long R2D2Terminate - a time stamp that R2d2 puts in right before termination.
    ●long LandoTerminate - a time stamp that Lando puts in right before termination.
    ●To get those timestamps, simply use System.currentTimeMillis().
    ●We will check that your timestamps make sense.
    ●Each timestamp is recorded by the specified name, e.g. only C3PO is allowed toset the value of C3POFinish. The totalAttacks member is recorded​
    only byHanSolo or C3PO​.
    ●You can add to this class members and methods as you see right
     */


	public static void init(String argsAt0, Input input) throws IOException {
		Gson gson = new Gson();
		Reader reader = null;
		try{
			reader = new FileReader(argsAt0);
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

			input.setAttacks(attacks);
			input.setEwoks(ewoks);
			input.setLando(lando);
			input.setR2D2(r2d2);


		} catch (Exception e){
			System.out.println("args[0] is not defined");
		}
		reader.close();
	}
}
