import { createStyles } from '@mantine/core';

const useStyles = createStyles((theme) => ({
  divider: {
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'stretch',
    alignItems: 'center',
    fontSize: theme.fontSizes.xs,
    color: theme.colors.gray[5],
    '&:before, &:after': {
      content: '""',
      flexGrow: 1,
      borderLeftWidth: 1,
      borderLeftStyle: 'solid',
      borderLeftColor: theme.colors.gray[4],
    },
  },
}));

export default function VerticalDivider({ label }: { label?: string }) {
  const { classes } = useStyles();

  return <div className={classes.divider}>{label}</div>;
}
