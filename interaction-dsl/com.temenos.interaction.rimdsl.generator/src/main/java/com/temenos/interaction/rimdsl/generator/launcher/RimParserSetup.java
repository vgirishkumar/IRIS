package com.temenos.interaction.rimdsl.generator.launcher;

import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.rim.DomainModel;
import org.eclipse.xtext.junit4.util.ParseHelper;

/**
 * @author kwieconkowski
 */

public class RimParserSetup {
    @Inject
    private ParseHelper<DomainModel> parserObject;

    public DomainModel parseToDomainModel(String rimFileContext) {
        try {
            return parserObject.parse(rimFileContext);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
