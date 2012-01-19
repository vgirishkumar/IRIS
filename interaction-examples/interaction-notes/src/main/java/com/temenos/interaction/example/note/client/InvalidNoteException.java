package com.temenos.interaction.example.note.client;

public class InvalidNoteException extends RuntimeException {
	private static final long serialVersionUID = 9136309724811030091L;

	public InvalidNoteException(Exception ex) {
        super(ex);
    }

    public InvalidNoteException() {}

}
