export default interface Service {
  name: string;
  deviceId: string;
  organizationId: string;
  version: number;
  description?: string;
  lastUpdateTime: string;
}
