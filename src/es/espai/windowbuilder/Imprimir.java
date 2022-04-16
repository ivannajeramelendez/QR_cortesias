package es.espai.windowbuilder;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Color;

public class Imprimir implements Printable {

	private BufferedImage imagen;
	
	private String club;
	
	private String registro;
	
	private String horaEntrada;
    
	public Imprimir(BufferedImage imagen,String club,String registro,String horaEntrada ) {
		this.club=club;
		this.registro=registro;
		this.horaEntrada=horaEntrada;
		this.imagen=imagen;
	}

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
             return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        g.drawImage(imagen, 0, 60,null);
        g2d.setColor(Color.black);
        g2d.setFont(new Font( "SansSerif", Font.BOLD, 15 ));
        g2d.drawString(club, 55, 15);
        g2d.setFont(new Font( "SansSerif", Font.BOLD, 8 ));
        g2d.drawString("Cuota de recuperacion de $100 por la perdida de QR", 0, 55);
        g2d.drawString("y la comprobacion de la propiedad del vehiculo. ",0,65);
        
        g2d.drawString("Folio: "+registro,65,imagen.getHeight()+60);
        g2d.drawString(horaEntrada,65,imagen.getHeight()+70);
        g2d.drawString("El club "+club+" no se hace",0,imagen.getHeight()+90);
        g2d.drawString("responsable de cualquier daño o robo, sea parcial",0,imagen.getHeight()+100);
        g2d.drawString("o total de tu automovil el hecho de ingresar al",0,imagen.getHeight()+110);
        g2d.drawString("estacionamiento implica una aceptacion tácita del ",0,imagen.getHeight()+120);
        g2d.drawString("Reglamento del Estacionamiento.", 0, imagen.getHeight()+130);
        
        
        
        
        return PAGE_EXISTS;
    }

}