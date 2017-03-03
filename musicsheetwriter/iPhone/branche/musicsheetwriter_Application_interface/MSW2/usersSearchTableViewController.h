//
//  usersSearchTableViewController.h
//  MSW2
//
//  Created by simon on 22/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"
#import "SubscriptionCell.h"

@interface usersSearchTableViewController : UITableViewController

@property (strong, nonatomic) NSMutableArray *pseudo;
@property (strong, nonatomic) NSMutableArray *idRow;
@property (strong, nonatomic) NSMutableArray *idRow2;
@property (strong, nonatomic) NSMutableArray *suscribeRow;

@end
