export async function checkMergeability(slack: Slack, gitHub: GitHub, secrets: Secrets): Promise<any> {
    const result: Result = await fetchGitHubMergeability(gitHub, secrets.gitHub)
    const recipient: string = secrets.slack.recipient
    return await notifySlack(slack)(recipient)(result)
}

async function fetchGitHubMergeability(gitHub: GitHub, secrets: GitHubSecrets): Promise<Result> {
    const prs = await gitHub.fetchOpenPullRequests(secrets)
    const unmergeablePrs = prs.map((pr: any) => pr.data).filter((pr: any) => !pr.mergeable)
    const isCalculating: boolean = prs.filter((pr: any) => pr.data.mergeable === null).length > 0
    return {
        prs: unmergeablePrs,
        isCalculating,
    }
}

const notifySlack = (slack: Slack) => (recipient: string) => (result: Result): Promise<any> => {
    const poster = slack.post(recipient)
    if (result.isCalculating) {
        return poster('Github is still calculating conflicts ¯\\_(ツ)_/¯')
    } else if (result.prs.length === 0) {
        return Promise.resolve('no conflicts, doing nothing');
    } else {
        const messages = result.prs.map(pr => `<${pr.html_url}|${pr.title}> has conflicts with master!`)
            .map(message => poster(message))
        return Promise.all(messages)
    }
}
