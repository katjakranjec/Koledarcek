
public class DanMesecLeto {
	
	private long dan;
	private int mesec;
	private long leto;
	
	public DanMesecLeto(long dan, int mesec, long leto) {
		this.dan = dan;
		this.mesec = mesec;
		this.leto = leto;
	}
	
    public long getDan() {
        return dan;
    }
	
    public int getMesec() {
        return mesec;
    }

    public long getLeto() {
        return leto;
    }
    
    public boolean obstaja() {
    	// Metoda vrne true, ce je datum obstaja, in false sicer.
    	
    	return (leto > 0 && mesec >= 0 && mesec < 12 && dan > 0 && dan <= new MesecLeto(mesec, leto).steviloDni());
    }

}
