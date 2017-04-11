import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class MostrarImagen 
{	
	public static void main( String[] args )
	{
		try
		{
			setLibraryPath();
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME );	      
			//Descargar imagen
			try(InputStream in = new URL("http://24.media.tumblr.com/tumblr_lfp3qax6Lm1qfmtrbo1_1280.jpg").openStream())
			{
				Files.copy(in, Paths.get("C:\\Segmentacion\\SourceImage.jpg"));
			}
			catch (IOException e) 
			{
				System.out.println("La imagen ya existe");
			}
			
			//Cargar imagen
			Mat src = Imgcodecs.imread("C:\\Segmentacion\\SourceImage.jpg");
			if (src.empty())
				System.out.println("Error cargando...");
			
			//Imagen a exportar
			File input = new File("C:\\Segmentacion\\SourceImage.jpg");
			BufferedImage image = ImageIO.read(input);
			Mat mat1 = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC1);
			Mat imgFinal = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC3);
			
			double[] aux = new double [3];
			aux[0] = 32;
			aux[1] = 25;
			aux[2] = 234;

			Imgproc.cvtColor(src, mat1, Imgproc.COLOR_RGB2GRAY);
			
			//Guardar imagen en gris
			Imgcodecs.imwrite("C:\\Segmentacion\\Gris.jpg", mat1);
			
			Mat gris = Imgcodecs.imread("C:\\Segmentacion\\Gris.jpg");
			
			//Cargar mask
			Mat mask = Imgcodecs.imread("C:\\Segmentacion\\mask.png");
			
			for( int x = 0; x < mask.rows(); x++ ) 
			{
				for( int y = 0; y < mask.cols(); y++ ) 
				{
					imgFinal.put(x, y, gris.get(x, y));
					if ( mask.get(x, y)[0] == 0 && mask.get(x, y)[1] == 0 && mask.get(x, y)[2] == 0) 
					{
						imgFinal.put(x, y, aux);
					}
				}
			}
			//Guardar imagen final
			Imgcodecs.imwrite("C:\\Segmentacion\\Final.jpg", imgFinal);
			
			//Limpiar
			File flsrc = new File ("C:\\Segmentacion\\SourceImage.jpg");
			flsrc.delete();
			File flgris = new File ("C:\\Segmentacion\\Gris.jpg");
			flgris.delete();
			
			System.out.println("Termino");
		}
		catch(Exception e)
		{
			System.out.println("fallo: " + e.getMessage());
		}
	}
	
	private static void setLibraryPath() 
	{

	    try 
	    {
	        System.setProperty("java.library.path", "libs");

	        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
	        fieldSysPath.setAccessible(true);
	        fieldSysPath.set(null, null);
	    } 
	    catch (Exception ex) 
	    {
	        ex.printStackTrace();
	        throw new RuntimeException(ex);
	    }

	}
}

