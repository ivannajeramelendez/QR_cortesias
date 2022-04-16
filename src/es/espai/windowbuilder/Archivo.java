package es.espai.windowbuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
/**Clase generada para poder leer el contenido del archivo application.propperties
 * 
 * @author Daniel García Velasco y Abimael Rueda Galindo
 *
 */
public  class Archivo {

	/**
	 * metodo estatico lee el contenido del archivo application.propperties y guarda los datos en un json para poder hacer uso de estos datos
	 * @return JSONObject application
	 * @throws IOException
	 */
	public static JSONObject inicializar() throws IOException {
		try {
			JSONObject application=new JSONObject();
			String cadena;
	        FileReader f = new FileReader("application.properties"); 
	         BufferedReader b = new BufferedReader(f);
	        while((cadena = b.readLine())!=null) {
	            String[]c=cadena.split("=");
	            application.put(c[0], c[1]);
	        }
	        b.close();
			return application;
		}catch(JSONException e) {
			
		}
		return null;
		
	}
}
