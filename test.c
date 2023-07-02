#include <stdio.h>

#include <readline/readline.h>
#include <readline/history.h>

void linehandler(char *line)
{
  printf("line: %s\n", line);
}

int main()
{
  rl_callback_handler_install("> ", linehandler);
  while (1)
  {
    rl_callback_read_char();
    char *saved_line;
    int saved_point;
    saved_point = rl_point;
    saved_line = rl_copy_text(0, rl_end);
    rl_replace_line("", 0);
    rl_redisplay();
    printf("\001\e[0;31m\002%s\001\e[0m\002", saved_line);
    rl_replace_line(saved_line, 0);
    rl_point = saved_point;
    rl_redisplay();
    free(saved_line);
    rl_redisplay();
  }
}
