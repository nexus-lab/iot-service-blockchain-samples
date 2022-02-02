import { Group, Text, ThemeIcon, UnstyledButton, createStyles } from '@mantine/core';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { Box as BoxIcon, Radio as RadioIcon, Send as SendIcon } from 'react-feather';

interface MainNavLinkProps {
  icon: React.ReactNode;
  color: string;
  label: string;
  link: string;
}

const useStyles = createStyles((theme) => ({
  button: {
    display: 'block',
    width: '100%',
    padding: theme.spacing.xs,
    borderRadius: theme.radius.sm,
    color: theme.black,

    '&:not(.active):hover': {
      backgroundColor: theme.colors.gray[0],
    },

    '&.active': {
      backgroundColor: theme.colors.blue[0],
    },
  },
}));

function MainNavLink({ icon, color, label, link }: MainNavLinkProps) {
  const { classes } = useStyles();
  const router = useRouter();
  const isActive = router.pathname.startsWith(link);

  return (
    <Link href={link} passHref>
      <UnstyledButton
        className={[classes.button, isActive ? 'active' : ''].filter(Boolean).join(' ')}>
        <Group>
          <ThemeIcon color={color}>{icon}</ThemeIcon>
          <Text size="sm">{label}</Text>
        </Group>
      </UnstyledButton>
    </Link>
  );
}

export default function MainNav() {
  const menus = [
    {
      color: 'blue',
      link: '/devices',
      label: 'Devices',
      icon: <BoxIcon size={16} />,
    },
    {
      color: 'lime',
      link: '/services',
      label: 'Services',
      icon: <RadioIcon size={16} />,
    },
    {
      color: 'violet',
      link: '/requests',
      label: 'Requests',
      icon: <SendIcon size={16} />,
    },
  ];

  return (
    <>
      {menus.map((menu) => (
        <MainNavLink key={menu.label} {...menu} />
      ))}
    </>
  );
}
