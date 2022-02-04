import axios from 'axios';

import ServiceRequestResponse from '../../types/requests';

export async function getServiceRequest(requestId: string) {
  const { data } = await axios.get<ServiceRequestResponse>('/api/requests', {
    params: { requestId },
  });
  return data;
}

export async function getServiceRequests(
  organizationId: string,
  deviceId: string,
  serviceName: string
) {
  const { data } = await axios.get<ServiceRequestResponse[]>('/api/requests', {
    params: { organizationId, deviceId, serviceName },
  });
  return data;
}
