package org.o7.Fire.Glopion.Patch.Mindustry;

import mindustry.ui.fragments.ChatFragment;
import mindustry.ui.fragments.Fragment;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Patch.TranslateChat;

public class ChatFragmentPatched extends ChatFragment {
    final ChatFragment cf;
    
    public ChatFragmentPatched(ChatFragment cf) {
        this.cf = cf;
    }
    
    @Override
    public Fragment container() {
        return cf.container();
    }
    
    @Override
    public void clearMessages() {
        cf.clearMessages();
    }
    
    @Override
    public void draw() {
        cf.draw();
    }
    
    @Override
    public void toggle() {
        cf.toggle();
    }
    
    @Override
    public void hide() {
        cf.hide();
    }
    
    @Override
    public void updateChat() {
        cf.updateChat();
    }
    
    @Override
    public void nextMode() {
        cf.nextMode();
    }
    
    @Override
    public void clearChatInput() {
        cf.clearChatInput();
    }
    
    @Override
    public void updateCursor() {
        cf.updateCursor();
    }
    
    @Override
    public boolean shown() {
        return cf.shown();
    }
    
    @Override
    public void addMessage(String message, String sender) {
        if (GlopionCore.translateChatSettings){
            if (message != null) TranslateChat.translate(message, TranslateChat.getTarget(), s -> {
                cf.addMessage(s, sender + TranslateChat.getMeta() + "[local]");
            });
        }
        cf.addMessage(message, sender);
    }
}
