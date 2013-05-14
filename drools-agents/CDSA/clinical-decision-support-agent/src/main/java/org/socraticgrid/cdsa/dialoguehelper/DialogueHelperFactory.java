/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.cdsa.dialoguehelper;

/**
 *
 * @author esteban
 */
public class DialogueHelperFactory {

    /**
     * This is not final so it can be changed in testing environments.
     * @param url 
     */
    public static Class<? extends DialogueHelperWrapper> dialogueHelperClass = DialogueHelperWrapperImpl.class;

    public static DialogueHelperWrapper newDialogueHelperWrapper(String url) {
        try {
            return dialogueHelperClass.getConstructor(String.class).newInstance(url);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static DialogueHelperWrapper newDialogueHelperWrapper(String url, org.drools.mas.Encodings enc) {
        try {
            return dialogueHelperClass.getConstructor(String.class, org.drools.mas.Encodings.class).newInstance(url, enc);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DialogueHelperWrapper newDialogueHelperWrapper(String url, int wSDLRetrievalTimeout) {
        try {
            return dialogueHelperClass.getConstructor(String.class, Integer.TYPE).newInstance(url, wSDLRetrievalTimeout);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DialogueHelperWrapper newDialogueHelperWrapper(String url, org.drools.mas.Encodings enc, int wSDLRetrievalTimeout) {
        try {
            return dialogueHelperClass.getConstructor(String.class, org.drools.mas.Encodings.class, Integer.TYPE ).newInstance(url, enc, wSDLRetrievalTimeout);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
