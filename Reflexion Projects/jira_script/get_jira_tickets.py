from jira.client import JIRA

def authent():
   #Get Jira Reflexion url
   jira_options={'server' : 'https://projects.reflexionhealth.com'}


   #Authorize username and password
   jira = JIRA(options=jira_options, basic_auth=('Jason', 'Doorsshut1'))
   return jira


#Function for getting sorted requirements by project
def product_requirements():
   
   jira = authent()

   #Open file to write to
   text_file = open('related_issues.txt', 'w')

   #Query issues and issue types
   issues = jira.search_issues('"Coding Traceability Source File(s)" IS NOT EMPTY',startAt=0, maxResults=1000)

   #sort the Coding Traceability Source Files(s) fields
   tracer = sorted([issue.fields.customfield_11000] for issue in issues)
   #Initialize num for incrementing
   num = 0


   #Loop through and assign a number to each summary
   for issue in issues:
      num += 1
      text_file.write('%d' % num)
      text_file.write('.) ')
      text_file.write(str(issue.fields.customfield_11000))
      text_file.write('\n')
   
   text_file.close()


if __name__ == '__main__':
   authent()
   product_requirements()
