package com.sunz.framework.system.receive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageEditor {
	public static  String Supported_Format="jpg|gif|png|bmp";
	private Color background=Color.white;
	private Image target;
	private Image source;
	private String defaultType="jpg";
	private int targetWidth=-1;
	private int targetHeight=-1;
	
	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	public void setBackground(Color background) {
		this.background = background;
	}
	public void setSource(Image source) {
		this.target=this.source = source;
	}
	public void setTargetWidth(int targetWidth) {
		this.targetWidth = targetWidth;
	}
	public void setTargetHeight(int targetHeight) {
		this.targetHeight = targetHeight;
	}
	
	
	public void loadImage(String fileName){
		try {
			setSource(ImageIO.read(new File(fileName)));
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	public Image translate(int x1,int y1,int x2,int y2,int x3,int y3){
		int w0=this.source.getWidth(null),h0=this.source.getHeight(null);
		double degree=Math.atan2(y2-y1, x2-x1);
		//save(imgTemp,"C:\\Users\\Xingzhe\\Desktop\\picEditor\\m.jpg");
		
		double ws=Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		double hs=Math.sqrt((x2-x3)*(x2-x3)+(y2-y3)*(y2-y3));
		
		// 若指定了大小，则缩放，否则直接使用选择区大小
		int w=this.targetWidth>0?this.targetWidth:(int)ws
				,h=this.targetHeight>0?this.targetHeight:(int)hs;
				
		double scaleX=w/ws,scaleY=h/hs;
		BufferedImage imgTemp=new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D) imgTemp.getGraphics();
		g.rotate(-degree, 0, 0);
		g.drawImage(this.target, (int)(-x1*scaleX), (int)(-y1*scaleY), (int)((-x1+w0)*scaleX), (int)((-y1+h0)*scaleY), 0, 0, w0, h0, null);
		
		g.dispose();
		
		this.target=imgTemp;
		
		return this.target;
	}
	
	
	public void save(Image img,String fileName){
		try {
			String fileType=fileName.substring(fileName.lastIndexOf("."));
			fileType=Supported_Format.contains(fileType)?fileType:this.defaultType;
			
			RenderedImage t=(RenderedImage) img;
			ImageIO.write(t, fileType, new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(String fileName){
		save(this.target,fileName);
	}
}
