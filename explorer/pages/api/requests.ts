import Joi from 'joi';
import { NextApiRequest, NextApiResponse } from 'next';

import { withCatchExceptions, withValidateQuery } from '../../components/api/middleware';
import sdk from '../../components/api/sdk';

function findServiceRequest(requestId: string) {
  return sdk.getServiceBroker().get(requestId);
}

function findServiceRequests(organizationId: string, deviceId: string, serviceName: string) {
  return sdk.getServiceBroker().getAll(organizationId, deviceId, serviceName);
}

async function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'GET') {
    res.status(405).end();
    return;
  }

  const { organizationId, deviceId, serviceName, requestId } = req.query as {
    organizationId?: string;
    deviceId?: string;
    serviceName?: string;
    requestId?: string;
  };

  if (requestId) {
    res.status(200).json(await findServiceRequest(requestId));
  } else {
    res.status(200).json(await findServiceRequests(organizationId!, deviceId!, serviceName!)); // eslint-disable-line @typescript-eslint/no-non-null-assertion
  }
}

export default withCatchExceptions(
  withValidateQuery(
    handler,
    Joi.object({
      organizationId: Joi.string(),
      deviceId: Joi.string(),
      serviceName: Joi.string(),
      requestId: Joi.string(),
    })
      .with('organizationId', ['deviceId', 'serviceName'])
      .with('deviceId', ['organizationId', 'serviceName'])
      .with('serviceName', ['organizationId', 'deviceId'])
      .xor('requestId', 'organizationId')
      .xor('requestId', 'deviceId')
      .xor('requestId', 'serviceName')
  )
);
