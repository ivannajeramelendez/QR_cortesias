package es.espai.windowbuilder;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.print.Paper;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class Inicio {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Inicio window = new Inicio();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Inicio() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon img = new ImageIcon("codigo.png");
		frame.setIconImage(img.getImage());
		JPanel panel = new JPanel();

		final JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("Crear codigo QR");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSONObject archivo;
				try {
					archivo = Archivo.inicializar();
					JSONObject usuario=new JSONObject();
					usuario.put("nombre",archivo.get("nombre"));
					usuario.put("nombreUsuario",archivo.get("nombreUsuario"));
					usuario.put("password",archivo.get("password"));
					
					String query=(String) archivo.get("login");
					URL url = new URL(query);
				    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				    conn.setConnectTimeout(5000);
				    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				    conn.setDoOutput(true);
				    conn.setDoInput(true);
				    conn.setRequestMethod("POST");

				    OutputStream os = conn.getOutputStream();
				    os.write(usuario.toString().getBytes("UTF-8"));
				    os.close();

				    // read the response
				    InputStream in = new BufferedInputStream(conn.getInputStream());
				    String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
				    JSONObject usuarioLog = new JSONObject(result);
		            in.close();
		            conn.disconnect();
		            
		            query=archivo.getString("crearQR");
					url = new URL(query);
				     conn = (HttpURLConnection) url.openConnection();
				     String basicAuth = "Bearer "+ usuarioLog.get("token");
			        conn.setRequestProperty ("Authorization", basicAuth);
			        
			        conn.setRequestProperty("Content-Type","application/json");
			        conn.setRequestMethod("GET");
			        BufferedReader inn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			        String output;

			        StringBuffer response = new StringBuffer();
			        while ((output = inn.readLine()) != null) {
			            response.append(output);
			        }
			        inn.close();
			        JSONObject json=new JSONObject(response.toString());
			        System.out.println(json.toString());
			        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			        Date date = dateFormat.parse(json.getString("horaEntrada"));
			        long unixTime = (long) date.getTime();
			        String recibo="RECIBO-"+json.get("idRegistro");
			        String uuid="-UUID-"+json.get("id");
			        String entrada="-Entrada-"+unixTime;
			        String club="-"+json.get("club");
			        String datos=recibo+uuid+entrada+club;
			        //String datos="RECIBO-000123-UUID-a5b7f36c-0a8b-4683-9ce8-f68d7c0262ac-entrada-1645333995"
			        		//+ "-A3";
			        ByteMatrix matrix;
			        Writer escritor = new QRCodeWriter();
			        matrix = escritor.encode(datos, BarcodeFormat.QR_CODE, 200, 200);
			             
			        BufferedImage imagen = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
			         
			        for(int y = 0; y < 200; y++) {
			            for(int x = 0; x < 200; x++) {
			                int grayValue = (matrix.get(x, y)) & 0xff;
			                imagen.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
			            }
			        }
			        ImageIcon icono = new ImageIcon(imagen);
			        JLabel etiqueta = new JLabel("");
			         
			        etiqueta.setIcon(icono);
			         
			        panel_1.removeAll();
			        panel_1.add(etiqueta);
			        frame.pack();
			        PrinterJob job = PrinterJob.getPrinterJob();
			        // define custom paper
			        Paper paper = new Paper();
			        paper.setSize(283.6573074154068, 421.1663066954644); // 1/72 inch
			        paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight()); // no margins

			        // custom page format
			        PageFormat pageFormat = new PageFormat();
			        pageFormat.setPaper(paper);
			        job.setPrintable(new Imprimir(imagen,archivo.getString("club"),json.getString("idRegistro"),json.getString("horaEntrada")),pageFormat);

			        boolean doPrint = job.printDialog();
			        if (doPrint) {
			               job.print();
			        }
						
		            /*query=archivo.getString("abrirPluma");
		            HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create(query+json.getString("idRegistro"))).header("Content-Type", "application/json").GET().build();
		            CompletableFuture<String> client = HttpClient.newHttpClient().sendAsync(request1, BodyHandlers.ofString()).thenApply(HttpResponse::body);
		        	String json2 = "";
		    		json2 = String.valueOf(client.get());*/
				
				} catch ( IOException | JSONException | WriterException | ParseException  | PrinterException /*| InterruptedException | ExecutionException */e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		panel.add(btnNewButton);
		
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
	}

}
