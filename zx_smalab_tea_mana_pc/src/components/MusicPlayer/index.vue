<template>
    <div class="music-player-wrapper">
        <!-- 悬浮球 -->
        <div class="music-ball" :class="{ 'is-playing': isPlaying, 'is-expanded': isExpanded }" :style="ballStyle"
            @click="toggleExpand">
            <div class="ball-inner">
                <img v-if="currentSong.cover" :src="currentSong.cover" class="cover-img"
                    :class="{ rotating: isPlaying }" />
                <el-icon v-else class="music-icon">
                    <Headset />
                </el-icon>
            </div>
            <div class="ball-ring" :style="ringStyle"></div>
        </div>

        <!-- 展开的播放器面板 -->
        <Transition name="player-slide">
            <div v-show="isExpanded" class="player-panel" :style="panelStyle">
                <div class="panel-header">
                    <span class="panel-title">音乐播放器</span>
                    <el-icon class="close-btn" @click.stop="isExpanded = false">
                        <Close />
                    </el-icon>
                </div>

                <!-- 当前播放歌曲信息 -->
                <div class="current-song">
                    <div class="song-cover" :style="{ backgroundImage: `url(${currentSong.cover})` }">
                        <div class="cover-mask"></div>
                    </div>
                    <div class="song-info">
                        <div class="song-name">{{ currentSong.name || '暂无歌曲' }}</div>
                        <div class="song-artist">{{ currentSong.artist || '未知歌手' }}</div>
                    </div>
                </div>

                <!-- 进度条 -->
                <div class="progress-bar">
                    <span class="time current">{{ formatTime(currentTime) }}</span>
                    <el-slider v-model="progress" :show-tooltip="false" @change="handleProgressChange" />
                    <span class="time duration">{{ formatTime(duration) }}</span>
                </div>

                <!-- 控制按钮 -->
                <div class="controls">
                    <el-icon class="control-btn" @click="playPrev">
                        <DArrowLeft />
                    </el-icon>
                    <el-icon class="control-btn play-btn" @click="togglePlay">
                        <VideoPlay v-if="!isPlaying" />
                        <VideoPause v-else />
                    </el-icon>
                    <el-icon class="control-btn" @click="playNext">
                        <DArrowRight />
                    </el-icon>
                </div>

                <!-- 音量控制 -->
                <div class="volume-control">
                    <el-icon>
                        <Mute v-if="volume === 0" />
                        <Microphone v-else />
                    </el-icon>
                    <el-slider v-model="volume" :max="100" size="small" @input="handleVolumeChange" />
                </div>

                <!-- 歌曲列表 -->
                <div class="playlist">
                    <div class="playlist-header">
                        <span>播放列表 ({{ playlist.length }})</span>
                    </div>
                    <div class="playlist-content">
                        <div v-for="(song, index) in playlist" :key="song.id" class="playlist-item"
                            :class="{ active: currentIndex === index }" @click="playSong(index)">
                            <span class="song-index">{{ index + 1 }}</span>
                            <div class="song-detail">
                                <span class="name">{{ song.name }}</span>
                                <span class="artist">{{ song.artist }}</span>
                            </div>
                            <el-icon v-if="currentIndex === index && isPlaying" class="playing-icon">
                                <Headset />
                            </el-icon>
                        </div>
                    </div>
                </div>
            </div>
        </Transition>

        <!-- 隐藏的audio元素 -->
        <audio ref="audioRef" :src="currentSong.url" @timeupdate="handleTimeUpdate" @ended="playNext"
            @loadedmetadata="handleLoadedMetadata" @play="isPlaying = true" @pause="isPlaying = false"></audio>
    </div>
</template>

<script setup lang="ts" name="MusicPlayer">
import { ref, computed, onMounted, watch } from "vue";
import { Headset, Close, VideoPlay, VideoPause, DArrowLeft, DArrowRight, Mute, Microphone } from "@element-plus/icons-vue";

interface Song {
    id: number;
    name: string;
    artist: string;
    cover: string;
    url: string;
}

// 状态
const isExpanded = ref(false);
const isPlaying = ref(false);
const currentIndex = ref(0);
const currentTime = ref(0);
const duration = ref(0);
const progress = ref(0);
const volume = ref(60);
const dominantColor = ref({ r: 102, g: 177, b: 255 });

// audio元素引用
const audioRef = ref<HTMLAudioElement | null>(null);

// 播放列表 - 网易云歌单 8792942606 中的歌曲
const playlist = ref<Song[]>([
    // { id: 1382596189, name: "少年", artist: "梦然", cover: "https://p1.music.126.net/wSMfGvFzOAYRU9fYdYZ1sA==/109951164627180052.jpg", url: "https://music.163.com/song/media/outer/url?id=1382596189.mp3" },
]);

// 当前歌曲
const currentSong = computed((): Song => playlist.value[currentIndex.value] || { id: 0, name: "", artist: "", cover: "", url: "" });

// 悬浮球样式
const ballStyle = computed(() => ({
    background: `linear-gradient(135deg, rgba(${dominantColor.value.r}, ${dominantColor.value.g}, ${dominantColor.value.b}, 0.9), rgba(${dominantColor.value.r * 0.7}, ${dominantColor.value.g * 0.7}, ${dominantColor.value.b * 0.7}, 0.9))`
}));

// 光环样式
const ringStyle = computed(() => ({
    borderColor: `rgba(${dominantColor.value.r}, ${dominantColor.value.g}, ${dominantColor.value.b}, 0.5)`
}));

// 面板样式
const panelStyle = computed(() => ({
    background: `linear-gradient(180deg, rgba(${dominantColor.value.r}, ${dominantColor.value.g}, ${dominantColor.value.b}, 0.15) 0%, var(--el-bg-color) 30%)`
}));

// 切换展开
const toggleExpand = () => {
    isExpanded.value = !isExpanded.value;
};

// 切换播放
const togglePlay = () => {
    if (!audioRef.value) return;
    if (isPlaying.value) {
        audioRef.value.pause();
    } else {
        audioRef.value.play();
    }
};

// 播放指定歌曲
const playSong = (index: number) => {
    currentIndex.value = index;
    setTimeout(() => {
        audioRef.value?.play();
    }, 100);
};

// 上一首
const playPrev = () => {
    currentIndex.value = currentIndex.value > 0 ? currentIndex.value - 1 : playlist.value.length - 1;
    setTimeout(() => {
        audioRef.value?.play();
    }, 100);
};

// 下一首
const playNext = () => {
    currentIndex.value = currentIndex.value < playlist.value.length - 1 ? currentIndex.value + 1 : 0;
    setTimeout(() => {
        audioRef.value?.play();
    }, 100);
};

// 时间更新
const handleTimeUpdate = () => {
    if (!audioRef.value) return;
    currentTime.value = audioRef.value.currentTime;
    if (duration.value > 0) {
        progress.value = (currentTime.value / duration.value) * 100;
    }
};

// 加载元数据
const handleLoadedMetadata = () => {
    if (!audioRef.value) return;
    duration.value = audioRef.value.duration;
};

// 进度变化
const handleProgressChange = (val: number) => {
    if (!audioRef.value) return;
    audioRef.value.currentTime = (val / 100) * duration.value;
};

// 音量变化
const handleVolumeChange = (val: number) => {
    if (!audioRef.value) return;
    audioRef.value.volume = val / 100;
};

// 格式化时间
const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, "0")}`;
};

// 从图片提取主色调
const extractColor = (imgUrl: string) => {
    const img = new Image();
    img.crossOrigin = "Anonymous";
    img.src = imgUrl;
    img.onload = () => {
        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");
        if (!ctx) return;
        canvas.width = 50;
        canvas.height = 50;
        ctx.drawImage(img, 0, 0, 50, 50);
        try {
            const imageData = ctx.getImageData(0, 0, 50, 50).data;
            let r = 0, g = 0, b = 0, count = 0;
            for (let i = 0; i < imageData.length; i += 4) {
                r += imageData[i];
                g += imageData[i + 1];
                b += imageData[i + 2];
                count++;
            }
            dominantColor.value = {
                r: Math.round(r / count),
                g: Math.round(g / count),
                b: Math.round(b / count)
            };
        } catch (e) {
            console.log("无法提取颜色，使用默认颜色");
        }
    };
};

// 监听歌曲变化，提取封面颜色
watch(() => currentSong.value.cover, (newCover) => {
    if (newCover) {
        extractColor(newCover);
    }
}, { immediate: true });

// 初始化音量
onMounted(() => {
    if (audioRef.value) {
        audioRef.value.volume = volume.value / 100;
    }
});
</script>

<style scoped lang="scss">
.music-player-wrapper {
    position: fixed;
    right: 30px;
    bottom: 30px;
    z-index: 9999;
}

// 悬浮球
.music-ball {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
    position: relative;

    &:hover {
        transform: scale(1.1);
        box-shadow: 0 6px 25px rgba(0, 0, 0, 0.3);
    }

    &.is-expanded {
        transform: scale(0.9);
    }

    .ball-inner {
        width: 46px;
        height: 46px;
        border-radius: 50%;
        background: var(--el-bg-color);
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;

        .cover-img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 50%;

            &.rotating {
                animation: rotate 10s linear infinite;
            }
        }

        .music-icon {
            font-size: 24px;
            color: var(--el-color-primary);
        }
    }

    .ball-ring {
        position: absolute;
        width: 66px;
        height: 66px;
        border-radius: 50%;
        border: 2px solid;
        opacity: 0;
        animation: none;
    }

    &.is-playing .ball-ring {
        animation: pulse 1.5s ease-out infinite;
    }
}

// 播放器面板
.player-panel {
    position: absolute;
    right: 0;
    bottom: 70px;
    width: 320px;
    border-radius: 16px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    overflow: hidden;
    backdrop-filter: blur(10px);
    border: 1px solid var(--el-border-color-lighter);

    .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        border-bottom: 1px solid var(--el-border-color-lighter);

        .panel-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--el-text-color-primary);
        }

        .close-btn {
            cursor: pointer;
            font-size: 18px;
            color: var(--el-text-color-secondary);
            transition: color 0.2s;

            &:hover {
                color: var(--el-color-primary);
            }
        }
    }

    .current-song {
        position: relative;
        padding: 20px;
        display: flex;
        align-items: center;
        gap: 16px;

        .song-cover {
            width: 70px;
            height: 70px;
            border-radius: 12px;
            background-size: cover;
            background-position: center;
            flex-shrink: 0;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
        }

        .song-info {
            flex: 1;
            overflow: hidden;

            .song-name {
                font-size: 16px;
                font-weight: 600;
                color: var(--el-text-color-primary);
                margin-bottom: 6px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .song-artist {
                font-size: 13px;
                color: var(--el-text-color-secondary);
            }
        }
    }

    .progress-bar {
        padding: 0 20px;
        display: flex;
        align-items: center;
        gap: 10px;

        .time {
            font-size: 12px;
            color: var(--el-text-color-secondary);
            min-width: 35px;

            &.current {
                text-align: right;
            }
        }

        :deep(.el-slider) {
            flex: 1;

            .el-slider__runway {
                height: 4px;
            }

            .el-slider__bar {
                height: 4px;
            }

            .el-slider__button-wrapper {
                display: none;
            }

            &:hover .el-slider__button-wrapper {
                display: block;
            }
        }
    }

    .controls {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 30px;
        padding: 20px;

        .control-btn {
            font-size: 24px;
            color: var(--el-text-color-primary);
            cursor: pointer;
            transition: all 0.2s;

            &:hover {
                color: var(--el-color-primary);
                transform: scale(1.1);
            }

            &.play-btn {
                font-size: 40px;
                color: var(--el-color-primary);
            }
        }
    }

    .volume-control {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 0 20px 15px;

        .el-icon {
            font-size: 18px;
            color: var(--el-text-color-secondary);
        }

        :deep(.el-slider) {
            flex: 1;
        }
    }

    .playlist {
        border-top: 1px solid var(--el-border-color-lighter);

        .playlist-header {
            padding: 12px 20px;
            font-size: 13px;
            color: var(--el-text-color-secondary);
            background: var(--el-fill-color-lighter);
        }

        .playlist-content {
            max-height: 200px;
            overflow-y: auto;

            .playlist-item {
                display: flex;
                align-items: center;
                padding: 10px 20px;
                cursor: pointer;
                transition: background 0.2s;

                &:hover {
                    background: var(--el-fill-color-light);
                }

                &.active {
                    background: var(--el-color-primary-light-9);

                    .name {
                        color: var(--el-color-primary);
                    }
                }

                .song-index {
                    width: 24px;
                    font-size: 13px;
                    color: var(--el-text-color-secondary);
                }

                .song-detail {
                    flex: 1;
                    overflow: hidden;

                    .name {
                        font-size: 14px;
                        color: var(--el-text-color-primary);
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                    }

                    .artist {
                        font-size: 12px;
                        color: var(--el-text-color-secondary);
                    }
                }

                .playing-icon {
                    color: var(--el-color-primary);
                    animation: pulse-icon 1s ease infinite;
                }
            }
        }
    }
}

// 动画
@keyframes rotate {
    from {
        transform: rotate(0deg);
    }

    to {
        transform: rotate(360deg);
    }
}

@keyframes pulse {
    0% {
        transform: scale(1);
        opacity: 0.6;
    }

    100% {
        transform: scale(1.5);
        opacity: 0;
    }
}

@keyframes pulse-icon {

    0%,
    100% {
        opacity: 1;
    }

    50% {
        opacity: 0.5;
    }
}

// 面板滑入动画
.player-slide-enter-active,
.player-slide-leave-active {
    transition: all 0.3s ease;
}

.player-slide-enter-from,
.player-slide-leave-to {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
}
</style>
