import Joi from 'joi';
import { NextApiRequest, NextApiResponse } from 'next';

export function withValidateQuery(
  handler: (req_: NextApiRequest, res_: NextApiResponse) => void | Promise<void>,
  schema: Joi.ObjectSchema
) {
  return (req: NextApiRequest, res: NextApiResponse) => {
    const { error } = schema.validate(req.query);
    if (typeof error !== 'undefined') {
      res.status(400).json({ details: error.details });
      return;
    }

    return handler(req, res);
  };
}

export function withCatchExceptions(
  handler: (req_: NextApiRequest, res_: NextApiResponse) => void | Promise<void>
) {
  return async (req: NextApiRequest, res: NextApiResponse) => {
    try {
      await handler(req, res);
    } catch ({ message, details }) {
      if (Array.isArray(details)) {
        if (details.some(({ message }: { message: string }) => message.endsWith('not found'))) {
          res.status(404).json({ message: 'resource not found' });
          return;
        }
      }

      res.status(500).json({ message });
    }
  };
}
