package com.temenos.interaction.commands.mule;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamException;

import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

public class InteractionCommandTransformer extends AbstractTransformer implements DiscoverableTransformer {

    private int weighting = DiscoverableTransformer. DEFAULT_PRIORITY_WEIGHTING + 1;
	private XMLWriter xmlWriter = new XMLWriter();
	
	public InteractionCommandTransformer() {
        registerSourceType(DataTypeFactory.create(ViewCommandWrapper.class));
        registerSourceType(DataTypeFactory.create(ActionCommandWrapper.class));
        setReturnDataType(DataTypeFactory.STRING);
        setName("InteractionCommandToXml");
	}
	
	@Override
	protected Object doTransform(Object src, String enc)
			throws TransformerException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			if (src instanceof ViewCommandWrapper)
				xmlWriter.toXml((ViewCommandWrapper) src, bos);
			if (src instanceof ActionCommandWrapper)
				xmlWriter.toXml((ActionCommandWrapper) src, bos);
			String result = new String(bos.toByteArray());
			return result;
		} catch (XMLStreamException e) {
			this.logger.error("Failed to transform command to xml", e);
			throw new TransformerException(this, e);
		}
	}

    public int getPriorityWeighting() {
        return weighting;
    }
  
    public void setPriorityWeighting(int weighting) {
        this.weighting = weighting;
    }

}
