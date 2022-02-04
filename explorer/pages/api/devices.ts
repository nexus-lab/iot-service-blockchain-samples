import Joi from 'joi';
import { NextApiRequest, NextApiResponse } from 'next';

import { withCatchExceptions, withValidateQuery } from '../../components/api/middleware';
import sdk from '../../components/api/sdk';

function findDevice(organizationId: string, deviceId: string) {
  return sdk.getDeviceRegistry().get(organizationId, deviceId);
}

function findDevices(organizationId: string) {
  return sdk.getDeviceRegistry().getAll(organizationId);
}

async function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'GET') {
    res.status(405).end();
    return;
  }

  const { organizationId, deviceId } = req.query as { organizationId: string; deviceId?: string };

  if (deviceId) {
    res.status(200).json(await findDevice(organizationId, deviceId));
  } else {
    res.status(200).json(await findDevices(organizationId));
  }
}

export default withCatchExceptions(
  withValidateQuery(
    handler,
    Joi.object({
      organizationId: Joi.string().required(),
      deviceId: Joi.string(),
    })
  )
);
