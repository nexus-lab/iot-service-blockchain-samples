import axios from 'axios';

import Service from '../../types/service';

export async function getService(organizationId: string, deviceId: string, serviceName: string) {
  const { data } = await axios.get<Service>('/api/services', {
    params: { organizationId, deviceId, serviceName },
  });
  return data;
}

export async function getServices(organizationId: string, deviceId: string) {
  const { data } = await axios.get<Service[]>('/api/services', {
    params: { organizationId, deviceId },
  });
  return data;
}
