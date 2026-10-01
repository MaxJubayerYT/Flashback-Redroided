package com.whaltermc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static org.bytedeco.ffmpeg.global.avutil.av_dict_set;

/**
 * On 1.21.11 Flashback uses JavaCV's stock FFmpegFrameRecorder (newer Flashback
 * versions ship their own fork of it). Android's MediaCodec encoders need the
 * "ndk_codec" option to be set before the video codec is opened.
 */
@Mixin(value = FFmpegFrameRecorder.class, remap = false)
public abstract class FFmpegFrameRecorderMixin {

    @Shadow
    private AVCodec video_codec;

    /**
     * ordinal = 0 is the video codec: startUnsafe() opens the video codec first
     * and the audio codec second, and both use the same avcodec_open2 signature.
     */
    @WrapOperation(
        method = "startUnsafe",
        remap = false,
        at = @At(
            value = "INVOKE",
            remap = false,
            ordinal = 0,
            target = "Lorg/bytedeco/ffmpeg/global/avcodec;avcodec_open2(Lorg/bytedeco/ffmpeg/avcodec/AVCodecContext;Lorg/bytedeco/ffmpeg/avcodec/AVCodec;Lorg/bytedeco/ffmpeg/avutil/AVDictionary;)I"
        )
    )
    private int flashbackRedroided$addNdkCodec(
            AVCodecContext context,
            AVCodec codec,
            AVDictionary options,
            Operation<Integer> original
    ) {
        // videoOptions is a protected field of FrameRecorder, so go through the public getter.
        FFmpegFrameRecorder self = (FFmpegFrameRecorder) (Object) this;

        if (video_codec != null
                && video_codec.name() != null
                && video_codec.name().getString().endsWith("_mediacodec")
                && self.getVideoOption("ndk_codec") == null) {

            av_dict_set(options, "ndk_codec", "1", 0);
        }

        return original.call(context, codec, options);
    }
}
