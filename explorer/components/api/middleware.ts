import Joi from 'joi';
import moment from 'moment';
import { NextApiRequest, NextApiResponse } from 'next';
import pc from 'picocolors';

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
    } catch (e) {
      const { message, details } = e as { message: string; details?: { message: string }[] };

      if (Array.isArray(details)) {
        if (details.some(({ message }: { message: string }) => message.endsWith('not found'))) {
          res.status(404).json({ message: 'resource not found' });
          return;
        }
      }

      console.warn(e);
      res.status(500).json({ message });
    }
  };
}

export function withLogging(
  handler: (req_: NextApiRequest, res_: NextApiResponse) => void | Promise<void>
) {
  const time = moment(new Date()).format('YYYY-MM-DD HH:mm:ss');

  return async (req: NextApiRequest, res: NextApiResponse) => {
    await handler(req, res);

    let color: (input: string | number | null | undefined) => string = pc.bgGreen;
    if (res.statusCode >= 500) {
      color = pc.bgRed;
    } else if (res.statusCode >= 400) {
      color = pc.bgYellow;
    }
    console.log(
      `${time} ${color(pc.black(res.statusCode))} ${pc.green(req.method)} ${req.url ?? 'unknown'}`
    );
  };
}
