package ca.ubc.cs.commandrecommender.model.cf;

//TODO: check over
public interface ItemFactory{

	Long toolForToolID(Long itemID);

	long[] tools();
	
}