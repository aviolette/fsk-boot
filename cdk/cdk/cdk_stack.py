from aws_cdk import (
    Stack,
)
from aws_cdk import aws_ec2 as ec2, aws_ecs as ecs, aws_ecs_patterns as ecs_patterns
import aws_cdk.aws_ecr as ecr

from constructs import Construct


class CdkStack(Stack):
    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        vpc = ec2.Vpc(self, "FskApp", max_azs=3)  # default is all AZs in region

        cluster = ecs.Cluster(self, "FskCluster", vpc=vpc)

        ecs_patterns.ApplicationLoadBalancedFargateService(
            self,
            "FreudianSearch",
            cluster=cluster,
            cpu=512,
            desired_count=1,
            task_image_options=ecs_patterns.ApplicationLoadBalancedTaskImageOptions(
                container_port=8080,
                image=ecs.ContainerImage.from_ecr_repository(
                    ecr.Repository.from_repository_name(
                        self, "FskRepository", "fskboot"
                    )
                )
            ),
            memory_limit_mib=2048,
            public_load_balancer=True,
        )
