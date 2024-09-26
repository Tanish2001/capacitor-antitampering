import { WebPlugin } from '@capacitor/core';
import type { AntiTamperingPlugin, AntiTamperingResult } from './definitions';
export declare class AntiTamperingWeb extends WebPlugin implements AntiTamperingPlugin {
    verify(): Promise<AntiTamperingResult>;
}
