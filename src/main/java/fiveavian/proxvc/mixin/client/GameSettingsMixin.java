package fiveavian.proxvc.mixin.client;

import fiveavian.proxvc.ProxVCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = GameSettings.class, remap = false)
public abstract class GameSettingsMixin {

    @Unique
    public OptionFloat voiceChatVolume;

    @Unique
    public OptionBoolean isMuted;

    @Unique
    public OptionBoolean usePushToTalk;

    @Unique
    public OptionString selectedInputDevice;

    @Inject(method = "<init>", at = @At(value = "NEW", target = "(Ljava/io/File;Ljava/lang/String;)Ljava/io/File;"))
    public void addOptions(Minecraft minecraft, File file, CallbackInfo ci){
        ProxVCClient.initOptions((GameSettings) (Object)this);
        this.voiceChatVolume = ProxVCClient.voiceChatVolume;
        this.isMuted = ProxVCClient.isMuted;
        this.usePushToTalk = ProxVCClient.usePushToTalk;
        this.selectedInputDevice = ProxVCClient.selectedInputDevice;
    }
}
