import Joi from 'joi';
import { NextApiRequest, NextApiResponse } from 'next';

import {
  withCatchExceptions,
  withLogging,
  withValidateQuery,
} from '../../components/api/middleware';
import sdk from '../../components/api/sdk';

function findService(organizationId: string, deviceId: string, serviceName: string) {
  return sdk.getServiceRegistry().get(organizationId, deviceId, serviceName);
}

function findServices(organizationId: string, deviceId: string) {
  return sdk.getServiceRegistry().getAll(organizationId, deviceId);
}

async function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'GET') {
    res.status(405).end();
    return;
  }

  const { organizationId, deviceId, serviceName } = req.query as {
    organizationId: string;
    deviceId: string;
    serviceName?: string;
  };

  if (serviceName) {
    res.status(200).json(await findService(organizationId, deviceId, serviceName));
  } else {
    res.status(200).json(await findServices(organizationId, deviceId));
  }
}

export default withLogging(
  withCatchExceptions(
    withValidateQuery(
      handler,
      Joi.object({
        organizationId: Joi.string().required(),
        deviceId: Joi.string().required(),
        serviceName: Joi.string(),
      })
    )
  )
);
