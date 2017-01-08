package hello;

public class Questions {

	private DomandaSingola domanda1;
	private DomandaSingola domanda2;
	private DomandaSingola domanda3;
	private DomandaSingola domanda4;
	
	public Questions(DomandaSingola d1, DomandaSingola d2, DomandaSingola d3, DomandaSingola d4){
		this.domanda1 = d1;
		this.domanda2 = d2;
		this.domanda3 = d3;
		this.domanda4 = d4;
	}
	
	public DomandaSingola getDomandaSingola1(){
		return domanda1;
	}
	
	public DomandaSingola getDomandaSingola2(){
		return domanda2;
	}
	
	public DomandaSingola getDomandaSingola3(){
		return domanda3;
	}
	
	public DomandaSingola getDomandaSingola4(){
		return domanda4;
	}

}
