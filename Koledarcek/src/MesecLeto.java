import java.util.Arrays;
import java.util.List;

public class MesecLeto {
	
	private int mesec;
	private long leto;
	private List<Integer> dolzineMesecev = Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
	
	public MesecLeto(int mesec, long leto) {
		this.mesec = mesec;
		this.leto = leto;
	}
	
    public int getMesec() {
        return mesec;
    }

    public long getLeto() {
        return leto;
    }
    
    public void setMesec(int mesec) {
    	this.mesec = mesec;
    }
    
    public void setLeto(long leto) {
    	this.leto = leto;
    }
    
    public boolean prestopnoLeto(long leto) {
    	// Metoda vrne true, ce je leto prestopno, in false v nasprotnem primeru.
    	
    	return (leto % 4 == 0) && (leto % 100 != 0 || leto % 400 == 0);
    }
    

    public int steviloDni() {
    	// Metoda vrne steviloDni v mesecu, tako da prebere ustrezno stevilo iz seznama in mu pristeje 1,
    	// ce je mesec februar in leto prestopno.
    	
    	if (mesec == 1 && prestopnoLeto(leto)) {
    		return dolzineMesecev.get(mesec) + 1;
    	} else {
    		return dolzineMesecev.get(mesec);
    	}
    }
    
    public long prviDan() {
    	// Metoda vrne prvi dan v mesecu, pri cemer uposteva, da je bil 1. januar 1 ponedeljek.
    	
    	int k = dolzineMesecev.subList(0, mesec).stream().mapToInt(Integer::intValue).sum(); // Prvi dan v mesecu je k-ti zaporedni dan tega leta
    	long steviloPrestopnih = (leto-1)/4 - (leto-1)/100 + (leto-1)/400; // Stevilo prestopnih let pred trenutnim letom
    	long prestopnoLetos = (prestopnoLeto(leto) && mesec > 1) ? 1:0; // Ali je trenutno leto prestopno in ali je februar ze bil?
    	long steviloDni = (leto-1)*365; // Stevilo dni, ki se je nabralo v prejsnjih letih
    	
    	// Vse sestejemo in vzamemo ostanek pri deljenju s 7
    	return (k + steviloPrestopnih + prestopnoLetos + steviloDni) % 7;
    	
    }

}
