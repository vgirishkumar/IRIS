package com.temenos.interaction.example.note.client;

import java.net.URI;

import com.temenos.interaction.example.app.client.Activity;

public class ReadNoteActivity extends Activity {

    @SuppressWarnings("unused")
	private final URI orderUri;
//    private OrderRepresentation currentOrderRepresentation;

    public ReadNoteActivity(URI orderUri) {
        this.orderUri = orderUri;
    }
/*
    public void readOrder() {
        try {
            currentOrderRepresentation = binding.retrieveOrder(orderUri);
            actions = new RepresentationHypermediaProcessor().extractNextActionsFromOrderRepresentation(currentOrderRepresentation);
        } catch (NotFoundException e) {
            actions = new Actions();
            actions.add(new PlaceOrderActivity());
        } catch (ServiceFailureException e) {
            actions = new Actions();
            actions.add(this);
        }
    }

    public ClientOrder getOrder() {
        return new ClientOrder(currentOrderRepresentation.getOrder());
    }
    */
}
