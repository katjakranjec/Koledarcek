import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Koledarcek extends JPanel {
	
	private MesecLeto mesecLeto = new MesecLeto(3, 2025);
	private List<String> meseci = new ArrayList<>(Arrays.asList("januar", "februar", "marec", "april", "maj", "junij", "julij", "avgust", "september", "oktober", "november", "december"));
	private List<String> dnevi = new ArrayList<>(Arrays.asList("PON", "TOR", "SRE", "ČET", "PET", "SOB", "NED"));

	public static final Color BLUSH = new Color(222, 93, 131);
	public static final Color PINK = new Color(255, 183, 197);
	public static final Color GHOST = new Color(248, 248, 255);
	
	private TexturePaint vzorec;
	
	public Koledarcek(List<DanMesecLeto> ponavljajociPrazniki, List<DanMesecLeto> posebniPrazniki) {
		
		//*******************************************************************************************
		// Ustvarimo JFrame frame, ki bo vseboval dva JPanela (moznosti koledarja in izris koledarja)
		
        JFrame frame = new JFrame("Koledarček");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
        
        //*******************************************************************************************
        // Ustvarimo JPanel moznosti, ki bo vseboval vse moznosti za nastavitev koledarja
        
        JPanel moznosti = new JPanel();
        moznosti.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Nekaj nastavitev Layouta
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Dodamo prazen prostor za lepsi izgled
        JLabel prazen1 = new JLabel(" ");
        gbc.gridy = 0;
        moznosti.add(prazen1, gbc);
        
        // Dodamo navodilo za uporabo JComboBoxa
        JLabel navodilo1 = new JLabel("Izberite mesec:");
        gbc.gridy = 1;
        moznosti.add(navodilo1, gbc);
        
        // Dodamo JComboBox z moznimi meseci
        String[] mozniMeseci = {"januar", "februar", "marec", "april", "maj", "junij", "julij", "avgust", "september", "oktober", "november", "december"};
        JComboBox<String> comboMeseci = new JComboBox<>(mozniMeseci);
        gbc.gridy = 2;
        moznosti.add(comboMeseci, gbc);
        
        // Dodamo navodilo za uporabo JTextFielda
        JLabel navodilo2 = new JLabel("Vpišite leto:");
        gbc.gridy = 3;
        moznosti.add(navodilo2, gbc);

        // Dodamo JTextField za vnos zelenega leta
        JTextField vnosLeto = new JTextField();
        gbc.gridy = 4;
        moznosti.add(vnosLeto, gbc);

        // Dodamo JButton, ki bo sprozil spremembo na koledarju
        JButton gumbMesecLeto = new JButton("Prikaži");
        gbc.gridy = 5;
        moznosti.add(gumbMesecLeto, gbc);
        
        // Dodamo prazen prostor za lepsi izgled
        JLabel prazen2 = new JLabel(" ");
        gbc.gridy = 6;
        moznosti.add(prazen2, gbc);
        
        // Dodamo navodilo za uporabo JTextFielda
        JLabel navodilo3 = new JLabel("Vpišite datum:");
        gbc.gridy = 7;
        moznosti.add(navodilo3, gbc);

        // Dodamo JTextField za vnos zelenega datuma
        JTextField vnosDanMesecLeto = new JTextField();
        gbc.gridy = 8;
        moznosti.add(vnosDanMesecLeto, gbc);
        
        // Dodamo JButton, ki bo sprozil spremembo na koledarju
        JButton gumbDanMesecLeto = new JButton("Prikaži");
        gbc.gridy = 9;
        moznosti.add(gumbDanMesecLeto, gbc);
        
        // Dodamo prazen prostor za lepsi izgled
        JLabel prazen3 = new JLabel(" ");
        gbc.gridy = 10;
        moznosti.add(prazen3, gbc);
        
        
        //*******************************************************************************************
        // Ustvarimo vzorec z diagonalnimi crtami, ki ga bomo uporabili za praznike
        
   		BufferedImage krpa = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dVzorec = krpa.createGraphics();

        g2dVzorec.setColor(Color.BLUE);
        g2dVzorec.setStroke(new BasicStroke(3));

        g2dVzorec.drawLine(0, 40, 40, 0);
        g2dVzorec.drawLine(0, 0, 0, 0);
        g2dVzorec.drawLine(0, 20, 20, 0);
        g2dVzorec.drawLine(40, 40, 40, 40);
        g2dVzorec.drawLine(20, 40, 40, 20);

        vzorec = new TexturePaint(krpa, new Rectangle(0, 0, 20, 20));
        
        //*******************************************************************************************
        // Ustvarimo JPanel izris, ki bo vseboval izrisan zeleni mesec
        
        JPanel izris = new JPanel() {
        	
        	@Override
        	protected void paintComponent(Graphics g) {
        		super.paintComponent(g);
        		
        		Graphics2D g2d = (Graphics2D) g;
        		g2d.setColor(Color.BLACK);
        		g2d.setStroke(new BasicStroke(2));

        		String tekst;
        		FontMetrics fm;
        		
        		// Dolocimo visino in sirino enega okenca na koledarju glede na velikost JPanela
                int sirinaOkenca = getWidth()/8;
                int visinaOkenca = getHeight()/17*2;
        		
                // Izpis meseca in leta
                g2d.setFont(new Font("SansSerif", Font.BOLD, visinaOkenca/2)); 
                fm = g2d.getFontMetrics();
                tekst = meseci.get(mesecLeto.getMesec()).toUpperCase() + " " + String.valueOf(mesecLeto.getLeto());
        		g2d.drawString(tekst, (getWidth() - fm.stringWidth(tekst))/2, visinaOkenca*2/3);
        		
        		// Izpis dnevov v tednu
        		g2d.setFont(new Font("SansSerif", Font.BOLD, visinaOkenca/4));
        		fm = g2d.getFontMetrics();
        		for (int i = 0; i < 7; i++) {
        			tekst = dnevi.get(i);
        			g2d.drawString(tekst, sirinaOkenca*(1+i) - fm.stringWidth(tekst)/2, visinaOkenca*4/3);
        		}

        		// Izris koledarja
        		for (int i = (int) mesecLeto.prviDan(); i < (int) mesecLeto.prviDan() + (int) mesecLeto.steviloDni(); i++) {
        			
        			// Dolocimo lokacijo zgornjega levega kota vsakega od okenc
        			int kot_x = sirinaOkenca/2 + sirinaOkenca*(i%7); // (i%7) nam pove dan v tednu
        			int kot_y = visinaOkenca*5/3 + visinaOkenca*(i/7); // (i/7) nam pove zaporedni teden
        			
        			// Nedelje pobarvamo z BLUSH, ostale dni pa s PINK
        			if (i % 7 == 6) {
        				g2d.setColor(BLUSH);
        				g2d.fillRect(kot_x, kot_y, sirinaOkenca, visinaOkenca);
        			} else {
        				g2d.setColor(PINK);
        				g2d.fillRect(kot_x, kot_y, sirinaOkenca, visinaOkenca);
        			}
        			
        			// Vsem okencem naredimo crno obrobo
        			g2d.setColor(Color.BLACK);
        			g2d.drawRect(kot_x, kot_y, sirinaOkenca, visinaOkenca);
        			
        			// V zgornji levi kot vsakega okenca vpisemo ustrezno stevilko (dan v mesecu)
        			g2d.drawString(String.valueOf(i-mesecLeto.prviDan()+1), (float) kot_x + sirinaOkenca/7, (float) kot_y + visinaOkenca/3); 
        		}
        		
        		// Gremo cez vse ponavljajoce praznike in jih izrisemo na koledar, ce se meseca ujemata
    			for (DanMesecLeto praznik: ponavljajociPrazniki) {
    				if (mesecLeto.getMesec() == praznik.getMesec()) {
    					
    					// Najti moramo prave koordinate okenca
    					int i = (int) mesecLeto.prviDan() + (int) praznik.getDan() - 1;
            			int kot_x = sirinaOkenca/2 + sirinaOkenca*(i%7);
            			int kot_y = visinaOkenca*5/3 + visinaOkenca*(i/7);
            			
            			// Okence pobarvamo z vzorcem
    					g2d.setPaint(vzorec);
    					g2d.fillRect(kot_x, kot_y, sirinaOkenca, visinaOkenca);
    					
    					// Se enkrat narisemo ustrezno stevilko, da je v ospredju
    					g2d.setColor(Color.BLACK);
    					g2d.drawString(String.valueOf(praznik.getDan()), (float) kot_x + sirinaOkenca/7, (float) kot_y + visinaOkenca/3);
    				}
    			}
    			
        		// Gremo cez vse posebne praznike in jih izrisemo na koledar, ce se meseca in leti ujemajo
    			for (DanMesecLeto praznik: posebniPrazniki) {
    				if (mesecLeto.getMesec() == praznik.getMesec() && mesecLeto.getLeto() == praznik.getLeto()) {
    					
    					// Najti moramo prave koordinate okenca
    					int i = (int) mesecLeto.prviDan() + (int) praznik.getDan() - 1;
            			int kot_x = sirinaOkenca/2 + sirinaOkenca*(i%7);
            			int kot_y = visinaOkenca*5/3 + visinaOkenca*(i/7);
            			
            			// Okence pobarvamo z vzorcem
    					g2d.setPaint(vzorec);
    					g2d.fillRect(kot_x, kot_y, sirinaOkenca, visinaOkenca);
    					
    					// Se enkrat narisemo ustrezno stevilko, da je v ospredju
    					g2d.setColor(Color.BLACK);
    					g2d.drawString(String.valueOf(praznik.getDan()), (float) kot_x + sirinaOkenca/7, (float) kot_y + visinaOkenca/3);
    				}
    			}
        	}
        };
        
    	// Dolocimo barvo ozadnja JPanela izris
    	izris.setBackground(GHOST);
    	
    	//***************************************************************************************************************
    	// Gumboma iz JPanela moznosti dodamo ActionListenerja, ki sprozita spremembo na JPanelu izris
        
        
    	// Gumb, ki sprozi spremembo glede na izbran mesec in vpisano leto
        gumbMesecLeto.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	// Preberemo izbrani mesec iz JComboBoxa
            	String izbraniMesec = (String) comboMeseci.getSelectedItem();
            	
            	// Poskusimo prebrati leto iz JTextFielda
                try {
                    long leto = Long.parseLong(vnosLeto.getText());
                    if (leto <= 0) {throw new IllegalArgumentException(); }
                    
                    // Ce je leto pozitivno celo stevilo, je veljavno in ustrezno nastavimo spremenljivko mesecLeto
                    mesecLeto.setMesec(meseci.indexOf(izbraniMesec));
                    mesecLeto.setLeto(leto);
                    
                    // Sprozimo ponovno risanje
                    izris.repaint();
                    
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Vpišite veljavno leto!", "Napačen vnos", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
     // Gumb, ki sprozi spremembo glede na izbran datum
        gumbDanMesecLeto.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                	
                	// Preberemo izbrani datum iz JTextFields
                	String vnos = vnosDanMesecLeto.getText();
                    String[] seznamDatum = vnos.split("\\.");

                    // Ce vnos ni razdeljen na 3 dele z dvema pikama, sprozimo izjemo
                    if (vnos.length() > 0 && vnos.charAt(vnos.length()-1) == '.') {throw new IllegalArgumentException(); } // prazen vnos in vnos, ki se konca s piko
                    if (seznamDatum.length > 3) {throw new IllegalArgumentException(); }
                    
                    // Shranimo datum, da lahko uporabimo metodo obstaja() iz DanMesecLeto
                    DanMesecLeto datum = new DanMesecLeto(Integer.parseInt(seznamDatum[0]), Integer.parseInt(seznamDatum[1])-1, Long.parseLong(seznamDatum[2]));
                    
                    // Ce datum obstaja, ustrezno nastavimo spremenljivko mesecLeto in sprozimo ponovno risanje
                    if (datum.obstaja()) {
                    	mesecLeto.setMesec(datum.getMesec());
                    	mesecLeto.setLeto(datum.getLeto());
                    	izris.repaint();
                	} else {
                		throw new IllegalArgumentException();
                    }
                    
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Vpišite veljaven datum (npr. 21.12.2025)!", "Napačen vnos", JOptionPane.ERROR_MESSAGE);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(frame, "Vpišite veljeven datum (npr. 21.12.2025)!", "Napačen vnos", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        //***************************************************************************************************************
        
        // Oba JPanela dodamo na frame; enega levo drugega desno
        frame.add(moznosti, BorderLayout.WEST);
        frame.add(izris, BorderLayout.CENTER);
        
        //***************************************************************************************************************
    	// Dodamo ComponentListener, ki bo sprozil spremembo v velikosti gumbov, teksta, JPanelov, ko spremenimo velikost frame-a
        
        frame.addComponentListener(new ComponentAdapter() {
        	
            @Override
            public void componentResized(ComponentEvent e) {
            	
            	// Visina in sirina frame-a
                int visina = frame.getHeight();
                int sirina = frame.getWidth();

                // Dolocimo dve novi pisavi (ena vecja, druga manjsa)
                Font fontMali = new Font("SansSerif", Font.PLAIN, visina/30);
                Font fontVeliki = new Font("SansSerif", Font.PLAIN, visina/30*4);
                
                // Vsem poljem/gumbom ustrezno spremenimo velikost
                comboMeseci.setFont(fontMali);
                vnosLeto.setFont(fontMali);
                gumbMesecLeto.setFont(fontMali);
                vnosDanMesecLeto.setFont(fontMali);
                gumbDanMesecLeto.setFont(fontMali);
                navodilo1.setFont(fontMali);
                navodilo2.setFont(fontMali);
                navodilo3.setFont(fontMali);
                prazen1.setFont(fontMali);
                prazen2.setFont(fontVeliki);
                prazen3.setFont(fontVeliki);
                
                // Spremenimo velikost JPanelov, da ohranimo razmerje
                moznosti.setPreferredSize(new Dimension(sirina*2/7, visina));
                izris.setPreferredSize(new Dimension(sirina*5/7, visina));

                // Ponovno izrisemo vse
                frame.revalidate();
                frame.repaint();
            }
        });
        
        // Nastavimo frame na vidno
        frame.setVisible(true);
         
	}
	
	public static void main(String[] args) {
		
		// Dolocimo pot do datoteke z datumi in ustvarimo prazna seznama, kamor bomo shranjevali praznike
    	String pot = "resources/prazniki.txt";
    	List<DanMesecLeto> ponavljajociPrazniki = new ArrayList<>();
    	List<DanMesecLeto> posebniPrazniki = new ArrayList<>();
    	
    	// Za ustvarjanje samostojne .exe datoteke
    	// InputStream inputStream = Koledarcek.class.getResourceAsStream(pot);
    	// try (BufferedReader prazniki = new BufferedReader(new InputStreamReader(inputStream))) {

        // Preberemo datoteko z BufferedReaderjem
        try (BufferedReader prazniki = new BufferedReader(new FileReader(pot))) {

            String vrstica;
            while ((vrstica = prazniki.readLine()) != null) {
            	
            	// Locimo vrstico na datum in oznako za ponavljajoc praznik
            	String[] seznamDatum = vrstica.split(",");
            	
            	// Locimo datum na dan, mesec, leto
            	String[] datum  = seznamDatum[0].split("\\.");
            	
            	// Praznike z oznako "P" dodamo v seznam ponavljajociPrazniki, ostale pa v seznam posebniPrazniki
            	if (seznamDatum[1].equals("P")) {
            		ponavljajociPrazniki.add(new DanMesecLeto(Integer.parseInt(datum[0]), Integer.parseInt(datum[1])-1, Integer.parseInt(datum[2])));
            	} else {
            		posebniPrazniki.add(new DanMesecLeto(Integer.parseInt(datum[0]), Integer.parseInt(datum[1])-1, Integer.parseInt(datum[2])));
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ustvarimo nov koledarcek
        new Koledarcek(ponavljajociPrazniki, posebniPrazniki);
    }

}
