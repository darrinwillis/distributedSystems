public class Sample implements SampleInterface {
    public int id;

    public Sample() {}

    public void setId(int i) {
	id = i;
    }
    
    public int getId() {
	return id;
    }
    
    public void setIdOther(Sample s, int i) {
	s.setId(i);
    }

    public int getIdOther(Sample s){
	return s.getId(); 
    }
}