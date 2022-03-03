require(ARTool)
require(car)
require(emmeans)
require(lme4)
require(plyr)
require(tidyr)

options(contrasts=c("contr.sum","contr.poly"))

data <- read.csv("../all_data.tsv", sep="\t")

data$P <- factor(data$P)
data$Condition <- factor(data$Condition)
data$Task <- factor(data$Task)

data <- aggregate(subset(data, select=c("P", "Condition", "Task", "Time", "Time.to.Target")), list(P = data$P, Condition=data$Condition, Task=data$Task), mean)
data <- subset(data, select=c(1, 2, 3, 7, 8))

time.art <- art(Time ~ Condition * Task + (1 | P), data=data)
time.aov <- anova(time.art)
print(time.aov)

time.cond.model <- artlm(time.art, "Condition")
time.cond.posthocs <- emmeans(time.cond.model, pairwise ~ Condition)
print(time.cond.posthocs)

time.task.model <- artlm(time.art, "Task")
time.task.posthocs <- emmeans(time.task.model, pairwise ~ Task)
print(time.task.posthocs)

timeto.art <- art(Time.to.Target ~ Condition * Task + (1 | P), data=data)
timeto.aov <- anova(timeto.art)
print(timeto.aov)

time.cond.model <- artlm(time.art, "Condition")
time.cond.posthocs <- emmeans(time.cond.model, pairwise ~ Condition)
print(time.cond.posthocs)

time.task.model <- artlm(time.art, "Task")
time.task.posthocs <- emmeans(time.task.model, pairwise ~ Task)
print(time.task.posthocs)
