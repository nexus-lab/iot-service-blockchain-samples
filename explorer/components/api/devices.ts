import axios from 'axios';

import Device from '../../types/device';

export async function getDevice(organizationId: string, deviceId: string) {
  const { data } = await axios.get<Device>('/api/devices', {
    params: { organizationId, deviceId },
  });
  return data;
}

export async function getDevices(organizationId: string) {
  const { data } = await axios.get<Device[]>('/api/devices', { params: { organizationId } });
  return data;
}
