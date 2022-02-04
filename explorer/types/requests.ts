export interface ServiceRequest {
  id: string;
  time: string;
  service: { name: string; deviceId: string; organizationId: string };
  method: string;
  args?: string[];
}

export interface ServiceResponse {
  requestId: string;
  time: string;
  statusCode?: number;
  returnValue?: string;
}

export default interface ServiceRequestResponse {
  request: ServiceRequest;
  response: ServiceResponse | null;
}
