package com.whaltermc.mixin;

import com.moulberry.flashback.exporting.FlashbackFFmpegFrameRecorder;
import com.llamalad7.mixinextras.sugar.Local;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.bytedeco.ffmpeg.global.avutil.av_dict_set;

@Mixin(FlashbackFFmpegFrameRecorder.class)
public abstract class FlashbackFFmpegFrameRecorderMixin {

    @Shadow
    private org.bytedeco.ffmpeg.avcodec.AVCodec video_codec;

    @Shadow
    private java.util.Map<String, String> videoOptions;

    @Inject(
        method = "startUnsafe",
        at = @At(
            value = "INVOKE",
            target = "Lorg/bytedeco/ffmpeg/global/avcodec;avcodec_open2(Lorg/bytedeco/ffmpeg/avcodec/AVCodecContext;Lorg/bytedeco/ffmpeg/avcodec/AVCodec;Lorg/bytedeco/ffmpeg/avutil/AVDictionary;)I"
        )
    )
    private void flashbackRedroided$addNdkCodec(
            CallbackInfo ci,
            @Local AVDictionary options
    ) {
        if (video_codec != null
                && video_codec.name() != null
                && video_codec.name().getString().endsWith("_mediacodec")
                && !videoOptions.containsKey("ndk_codec")) {

            av_dict_set(options, "ndk_codec", "1", 0);
        }
    }
}