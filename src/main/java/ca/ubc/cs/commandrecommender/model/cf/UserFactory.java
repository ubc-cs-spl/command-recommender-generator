package ca.ubc.cs.commandrecommender.model.cf;

//TODO: check over
public interface UserFactory{
	
	public Long userForUserId(Long id);
	public long[] users();
}