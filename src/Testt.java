import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.io.Charsets;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Testt {

	static int sayac = 0;
	static int counter = 0;
	static String lastResult = "";
	static String safeWords[] = { "sozluk", "haber", "oyun", "sczluk", "bilisim", "dizi", "yemek", "mp3", "sanayi",
			"hastane", "tvizle", "sepet", "gazete", "qazete", "bi|isim", "firma", "sarki", "soz|uk", "filmizle",
			"fi|mizle", "diziizle", "yatirim", "youtube", "qov,tr", "emlak", "qov.tr", "kitap", "sinav" };

	public static void main(String[] args) throws HeadlessException, AWTException, IOException {

		for (int i = 0; i < 300; i++) {
			if (checkAnomaly()) {
				dosmth();
			} else {
				closeWindowAndSkip();
			}
			try {
				Thread.sleep(3000); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

	}

	public static void closeWindowAndSkip() throws AWTException {
		mouseMove(583, 73);
		closeWindow();
		sleep(1000);
		skip();
	}
	
	public static void clickESC() throws AWTException{
		mouseMove(712, 79);
		doESC();
	}
	
	public static void doESC(){
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
			robot.keyRelease(KeyEvent.VK_ESCAPE);

		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static void closeWindow() {
		try {
			Robot robot = new Robot();
			// Simulate a key press
			robot.keyPress(KeyEvent.VK_ALT);
			robot.keyPress(KeyEvent.VK_F4);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_F4);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkAnomaly() throws IOException, HeadlessException, AWTException {
		BufferedImage image = new Robot()
				.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		BufferedImage croppedImage = image.getSubimage(192, 134, 164, 21);

		ImageIO.write(croppedImage, "png", new File("check.png"));

		File imageFile = new File("check.png");
		ITesseract instance = new Tesseract();
		String result = null;
		try {
			result = instance.doOCR(imageFile);
//			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result.contains("https://prod.uh")) {
			return true;
		} else {
			return false;
		}
	}

	public static void dosmth() throws HeadlessException, AWTException, IOException {
		HashMap<String, Integer> sites = new HashMap<String, Integer>();
		BufferedImage image = new Robot()
				.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		BufferedImage croppedImage = image.getSubimage(653, 104, 330, 26);

		ImageIO.write(croppedImage, "png", new File("1.png"));

		File imageFile = new File("1.png");
		ITesseract instance = new Tesseract(); // JNA Interface Mapping
		// ITesseract instance = new Tesseract1(); // JNA Direct Mapping
		String res = null;
		try {
			String result = instance.doOCR(imageFile);

			if (result.length() == 0) {
				return;
			}

			if (result.contains("//")) {
				res = result.replace("//", "");
				if (res.contains("/")) {
					res = res.split("/")[0];
				}
			} else {
				if (result.contains("/")) {
					res = result.split("/")[0];
				} else {
					res = result;
				}
			}
			if (res.contains("\n") || res.contains(" ")) {
				res = res.replaceAll("\\s+", "");
			}
			res = res.replaceAll("([\\ud800-\\udbff\\udc00-\\udfff])", "");
			// System.out.println(res);
		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}

		try (BufferedReader br = new BufferedReader(new FileReader("sites.txt"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println(sCurrentLine);
				sites.put(sCurrentLine.split(" ")[0], Integer.parseInt(sCurrentLine.split(" ")[1]));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (lastResult.equals(res)) {
			counter++;
			if (counter == 5) {
				clickESC();
			}
		} else {
			lastResult = res;
			counter = 0;
		}

		Integer val = sites.get(res);
		if (val != null) {
			sayac++;
			// System.out.println("Burda zaten var beya");
			if (val == 1) {
				chooseOne(1);
			} else if (val == 2) {
				chooseOne(2);
			} else if (val == 3) {
				chooseOne(3);
			} else if (val == 4) {
				chooseOne(4);
			}
			// System.out.println(sayac);
		} else {
			// System.out.println("Ekle bu yokmus");
			// try {
			// PrintWriter out = new PrintWriter(new BufferedWriter(new
			// FileWriter("sites.txt", true)));
			// Scanner scanner = new Scanner(System.in);
			// int type = scanner.nextInt();
			// out.print("\n" + res + " " + type);
			// out.close();
			// } catch (IOException e) {
			// // exception handling left as an exercise for the reader
			// }
//			System.out.println(res);
			checkThenSkip(res);
			// skip();
		}
	}

	public static void mouseMove(int x, int y) throws AWTException {
		Robot bot = new Robot();
		bot.mouseMove(x, y);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public static void checkThenSkip(String res) throws AWTException {
		if (isContainsSafeWords(res)) {
			sayac++;
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("sites.txt", true)));
				out.print("\n" + res + " " + 1);
				out.close();
			} catch (IOException e) {
				// exception handling left as an exercise for the reader
			}
			chooseOne(1);
		} else {
			System.out.println(res);
			skip();
		}
	}

	public static boolean isContainsSafeWords(String res) {
		for (int i = 0; i < safeWords.length; i++) {
			if (res.contains(safeWords[i])) {
				return true;
			}
		}
		return false;
	}

	public static void skip() throws AWTException {
		mouseMove(422, 287);
		sleep(1000);
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public static void chooseOne(int option) throws AWTException {
		if (option == 1) {
			mouseMove(75, 820);
			sleep(100);
			mouseMove(75, 820);
			sleep(100);
			mouseMove(250, 878);
			sleep(500);
			mouseMove(250, 878);
		} else if (option == 2) {
			mouseMove(75, 408);
			sleep(100);
			mouseMove(75, 408);
			sleep(100);
			mouseMove(250, 878);
			sleep(500);
			mouseMove(250, 878);
		} else if (option == 3) {
			mouseMove(75, 660);
			sleep(100);
			mouseMove(75, 660);
			sleep(100);
			mouseMove(250, 878);
			sleep(500);
			mouseMove(250, 878);
		} else if (option == 4) {
			mouseMove(75, 498);
			sleep(100);
			mouseMove(75, 498);
			sleep(100);
			mouseMove(250, 878);
			sleep(500);
			mouseMove(250, 878);
		}
	}

}
