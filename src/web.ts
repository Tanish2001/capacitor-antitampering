import { WebPlugin } from '@capacitor/core';

import type { AntiTamperingPlugin, AntiTamperingResult } from './definitions';

export class AntiTamperingWeb extends WebPlugin implements AntiTamperingPlugin {
  async verify(): Promise<AntiTamperingResult> {
    throw new Error('Method not implemented.');
  }
}
