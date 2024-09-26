export interface AntiTamperingPlugin {
    verify(): Promise<AntiTamperingResult>;
}
export interface AntiTamperingResult {
    status?: string;
    assetsCount: number;
    messages?: string;
    finalVerdict?:string;
    tamperFileNames?:any;
}
